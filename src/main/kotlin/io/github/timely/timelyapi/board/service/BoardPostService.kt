package io.github.timely.timelyapi.board.service

import io.github.timely.timelyapi.board.dto.BoardPostDto
import io.github.timely.timelyapi.board.model.BoardPost
import io.github.timely.timelyapi.board.repository.BoardCommentRepository
import io.github.timely.timelyapi.board.repository.BoardPostRepository
import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardPostService(
    private val boardPostRepository: BoardPostRepository,
    private val boardCommentRepository: BoardCommentRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createPost(authorUserSn: Long, request: BoardPostDto.CreateRequest): BoardPostDto.Response {
        validatePostValues(request.category, request.status, request.title, request.content)

        return boardPostRepository.save(
            BoardPost(
                authorUserSn = authorUserSn,
                category = request.category.trim(),
                status = request.status.trim(),
                title = request.title.trim(),
                content = request.content.trim()
            )
        ).toResponse()
    }

    @Transactional(readOnly = true)
    fun searchPosts(
        category: String?,
        status: String?,
        keyword: String?,
        pageable: Pageable
    ): PageResponse<BoardPostDto.SimpleResponse> {
        val page = boardPostRepository.searchActivePosts(
            category = category.normalized(),
            status = status.normalized(),
            keyword = keyword.normalized(),
            pageable = pageable
        ).map { it.toSimpleResponse() }

        return PageResponse.from(page)
    }

    @Transactional
    fun getPost(boardPostSn: Long): BoardPostDto.Response {
        val post = getActivePost(boardPostSn)
        post.viewCnt += 1
        return post.toResponse()
    }

    @Transactional
    fun updatePost(boardPostSn: Long, request: BoardPostDto.UpdateRequest): BoardPostDto.Response {
        validatePostValues(request.category, request.status, request.title, request.content)

        val post = getActivePost(boardPostSn)
        post.category = request.category.trim()
        post.status = request.status.trim()
        post.title = request.title.trim()
        post.content = request.content.trim()

        return post.toResponse()
    }

    @Transactional
    fun deletePost(boardPostSn: Long) {
        val post = getActivePost(boardPostSn)
        post.useYn = "N"
    }

    internal fun getActivePost(boardPostSn: Long): BoardPost {
        return boardPostRepository.findByBoardPostSnAndUseYn(boardPostSn, "Y")
            ?: throw IllegalArgumentException("Board post not found")
    }

    private fun validatePostValues(category: String, status: String, title: String, content: String) {
        require(category.isNotBlank()) { "Category must not be blank" }
        require(status.isNotBlank()) { "Status must not be blank" }
        require(title.isNotBlank()) { "Title must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn("BOARD_CATEGORY", category.trim(), "Y")) {
            "Invalid board category"
        }
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn("BOARD_STATUS", status.trim(), "Y")) {
            "Invalid board status"
        }
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun BoardPost.toSimpleResponse() =
        BoardPostDto.SimpleResponse(
            boardPostSn = boardPostSn!!,
            authorUserSn = authorUserSn,
            authorName = userRepository.findById(authorUserSn).orElse(null)?.userNm,
            category = category,
            status = status,
            title = title,
            viewCnt = viewCnt,
            commentCount = boardCommentRepository.countByBoardPostSnAndUseYn(boardPostSn!!, "Y"),
            createDt = createDt,
            updateDt = updateDt
        )

    private fun BoardPost.toResponse() =
        BoardPostDto.Response(
            boardPostSn = boardPostSn!!,
            authorUserSn = authorUserSn,
            authorName = userRepository.findById(authorUserSn).orElse(null)?.userNm,
            category = category,
            status = status,
            title = title,
            content = content,
            viewCnt = viewCnt,
            commentCount = boardCommentRepository.countByBoardPostSnAndUseYn(boardPostSn!!, "Y"),
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
