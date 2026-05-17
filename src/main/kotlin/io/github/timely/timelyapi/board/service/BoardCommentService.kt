package io.github.timely.timelyapi.board.service

import io.github.timely.timelyapi.board.dto.BoardCommentDto
import io.github.timely.timelyapi.board.model.BoardComment
import io.github.timely.timelyapi.board.repository.BoardCommentRepository
import io.github.timely.timelyapi.common.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardCommentService(
    private val boardCommentRepository: BoardCommentRepository,
    private val boardPostService: BoardPostService
) {

    @Transactional
    fun createComment(authorUserSn: Long, boardPostSn: Long, request: BoardCommentDto.CreateRequest): BoardCommentDto.Response {
        require(request.content.isNotBlank()) { "Content must not be blank" }
        boardPostService.getActivePost(boardPostSn)

        return boardCommentRepository.save(
            BoardComment(
                boardPostSn = boardPostSn,
                authorUserSn = authorUserSn,
                content = request.content.trim()
            )
        ).toResponse()
    }

    @Transactional(readOnly = true)
    fun searchComments(boardPostSn: Long, pageable: Pageable): PageResponse<BoardCommentDto.Response> {
        boardPostService.getActivePost(boardPostSn)
        val page = boardCommentRepository.findActiveCommentsByBoardPostSn(boardPostSn, pageable)
            .map { it.toResponse() }

        return PageResponse.from(page)
    }

    @Transactional(readOnly = true)
    fun getComment(boardPostSn: Long, boardCommentSn: Long): BoardCommentDto.Response {
        val comment = getActiveComment(boardCommentSn)
        require(comment.boardPostSn == boardPostSn) { "Board comment not found" }
        return comment.toResponse()
    }

    @Transactional
    fun updateComment(
        boardPostSn: Long,
        boardCommentSn: Long,
        request: BoardCommentDto.UpdateRequest
    ): BoardCommentDto.Response {
        require(request.content.isNotBlank()) { "Content must not be blank" }

        val comment = getActiveComment(boardCommentSn)
        require(comment.boardPostSn == boardPostSn) { "Board comment not found" }
        comment.content = request.content.trim()

        return comment.toResponse()
    }

    @Transactional
    fun deleteComment(boardPostSn: Long, boardCommentSn: Long) {
        val comment = getActiveComment(boardCommentSn)
        require(comment.boardPostSn == boardPostSn) { "Board comment not found" }
        comment.useYn = "N"
    }

    private fun getActiveComment(boardCommentSn: Long): BoardComment {
        return boardCommentRepository.findByBoardCommentSnAndUseYn(boardCommentSn, "Y")
            ?: throw IllegalArgumentException("Board comment not found")
    }

    private fun BoardComment.toResponse() =
        BoardCommentDto.Response(
            boardCommentSn = boardCommentSn!!,
            boardPostSn = boardPostSn,
            authorUserSn = authorUserSn,
            content = content,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
