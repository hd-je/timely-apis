package io.github.timely.timelyapi.board.service

import io.github.timely.timelyapi.board.dto.BoardCommentDto
import io.github.timely.timelyapi.board.model.BoardComment
import io.github.timely.timelyapi.board.model.BoardCommentLike
import io.github.timely.timelyapi.board.repository.BoardCommentLikeRepository
import io.github.timely.timelyapi.board.repository.BoardCommentRepository
import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.user.repository.UserRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardCommentService(
    private val boardCommentRepository: BoardCommentRepository,
    private val boardCommentLikeRepository: BoardCommentLikeRepository,
    private val boardPostService: BoardPostService,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager
) {

    @Transactional
    fun createComment(
        authorUserSn: Long,
        companySn: Long,
        boardPostSn: Long,
        request: BoardCommentDto.CreateRequest
    ): BoardCommentDto.Response {
        require(request.content.isNotBlank()) { "Content must not be blank" }
        boardPostService.getActivePost(companySn, boardPostSn)
        request.parentCommentSn?.let { parentCommentSn ->
            val parentComment = getActiveComment(boardPostSn, parentCommentSn)
            require(parentComment.parentCommentSn == null) { "Nested replies are not allowed" }
        }

        val comment = boardCommentRepository.save(
            BoardComment(
                boardPostSn = boardPostSn,
                authorUserSn = authorUserSn,
                parentCommentSn = request.parentCommentSn,
                content = request.content.trim()
            )
        )
        entityManager.flush()
        entityManager.refresh(comment)

        return comment.toResponse(authorUserSn)
    }

    @Transactional(readOnly = true)
    fun searchComments(
        userSn: Long,
        companySn: Long,
        boardPostSn: Long,
        pageable: Pageable
    ): PageResponse<BoardCommentDto.Response> {
        boardPostService.getActivePost(companySn, boardPostSn)
        val page = boardCommentRepository.findActiveCommentsByBoardPostSn(boardPostSn, pageable)
            .map { it.toResponse(userSn) }

        return PageResponse.from(page)
    }

    @Transactional(readOnly = true)
    fun getComment(userSn: Long, companySn: Long, boardPostSn: Long, boardCommentSn: Long): BoardCommentDto.Response {
        boardPostService.getActivePost(companySn, boardPostSn)
        val comment = getActiveComment(boardPostSn, boardCommentSn)
        return comment.toResponse(userSn)
    }

    @Transactional
    fun updateComment(
        userSn: Long,
        companySn: Long,
        boardPostSn: Long,
        boardCommentSn: Long,
        request: BoardCommentDto.UpdateRequest
    ): BoardCommentDto.Response {
        require(request.content.isNotBlank()) { "Content must not be blank" }

        boardPostService.getActivePost(companySn, boardPostSn)
        val comment = getActiveComment(boardPostSn, boardCommentSn)
        validateCommentAuthor(comment, userSn)
        comment.content = request.content.trim()

        return comment.toResponse(userSn)
    }

    @Transactional
    fun deleteComment(userSn: Long, companySn: Long, boardPostSn: Long, boardCommentSn: Long) {
        boardPostService.getActivePost(companySn, boardPostSn)
        val comment = getActiveComment(boardPostSn, boardCommentSn)
        validateCommentAuthor(comment, userSn)
        val commentSn = comment.boardCommentSn!!
        boardCommentLikeRepository.deactivateByCommentThread(commentSn)
        boardCommentRepository.deactivateCommentThread(commentSn)
    }

    @Transactional
    fun likeComment(userSn: Long, companySn: Long, boardPostSn: Long, boardCommentSn: Long) {
        boardPostService.getActivePost(companySn, boardPostSn)
        val comment = getActiveComment(boardPostSn, boardCommentSn)
        val commentSn = comment.boardCommentSn!!
        val like = boardCommentLikeRepository.findByBoardCommentSnAndUserSn(commentSn, userSn)

        if (like == null) {
            boardCommentLikeRepository.save(BoardCommentLike(boardCommentSn = commentSn, userSn = userSn))
        } else {
            like.useYn = "Y"
        }
    }

    @Transactional
    fun unlikeComment(userSn: Long, companySn: Long, boardPostSn: Long, boardCommentSn: Long) {
        boardPostService.getActivePost(companySn, boardPostSn)
        val comment = getActiveComment(boardPostSn, boardCommentSn)
        boardCommentLikeRepository.findByBoardCommentSnAndUserSn(comment.boardCommentSn!!, userSn)?.useYn = "N"
    }

    private fun getActiveComment(boardCommentSn: Long): BoardComment {
        return boardCommentRepository.findByBoardCommentSnAndUseYn(boardCommentSn, "Y")
            ?: throw IllegalArgumentException("Board comment not found")
    }

    private fun getActiveComment(boardPostSn: Long, boardCommentSn: Long): BoardComment {
        val comment = getActiveComment(boardCommentSn)
        require(comment.boardPostSn == boardPostSn) { "Board comment not found" }
        return comment
    }

    private fun validateCommentAuthor(comment: BoardComment, userSn: Long) {
        require(comment.authorUserSn == userSn) { "Only the comment author can modify this board comment" }
    }

    private fun BoardComment.toResponse(userSn: Long) =
        BoardCommentDto.Response(
            boardCommentSn = boardCommentSn!!,
            boardPostSn = boardPostSn,
            authorUserSn = authorUserSn,
            parentCommentSn = parentCommentSn,
            authorName = userRepository.findById(authorUserSn).orElse(null)?.userNm,
            content = content,
            useYn = useYn,
            likeCount = boardCommentLikeRepository.countByBoardCommentSnAndUseYn(boardCommentSn!!, "Y"),
            likedByMe = boardCommentLikeRepository.existsByBoardCommentSnAndUserSnAndUseYn(
                boardCommentSn!!,
                userSn,
                "Y"
            ),
            createDt = createDt,
            updateDt = updateDt
        )
}
