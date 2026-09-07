package io.github.timely.timelyapi.board.service

import io.github.timely.timelyapi.board.dto.BoardPostDto
import io.github.timely.timelyapi.board.model.BoardPost
import io.github.timely.timelyapi.board.model.BoardPostBookmark
import io.github.timely.timelyapi.board.model.BoardPostLike
import io.github.timely.timelyapi.board.repository.BoardCommentLikeRepository
import io.github.timely.timelyapi.board.repository.BoardCommentRepository
import io.github.timely.timelyapi.board.repository.BoardPostBookmarkRepository
import io.github.timely.timelyapi.board.repository.BoardPostDetailProjection
import io.github.timely.timelyapi.board.repository.BoardPostLikeRepository
import io.github.timely.timelyapi.board.repository.BoardPostRepository
import io.github.timely.timelyapi.board.repository.BoardPostSummaryProjection
import io.github.timely.timelyapi.board.repository.RecentBookmarkProjection
import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardPostService(
    private val boardPostRepository: BoardPostRepository,
    private val boardCommentRepository: BoardCommentRepository,
    private val boardCommentLikeRepository: BoardCommentLikeRepository,
    private val boardPostBookmarkRepository: BoardPostBookmarkRepository,
    private val boardPostLikeRepository: BoardPostLikeRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createPost(authorUserSn: Long, companySn: Long, request: BoardPostDto.CreateRequest): BoardPostDto.Response {
        validatePostValues(request.category, request.status, request.title, request.content)

        val categoryNames = getCodeNameMap("BOARD_CATEGORY")
        val statusNames = getCodeNameMap("BOARD_STATUS")

        return boardPostRepository.save(
            BoardPost(
                authorUserSn = authorUserSn,
                companySn = companySn,
                category = request.category.trim(),
                status = request.status.trim(),
                title = request.title.trim(),
                content = request.content.trim()
            )
        ).toResponse(authorUserSn, categoryNames, statusNames)
    }

    @Transactional(readOnly = true)
    fun searchPosts(
        userSn: Long,
        companySn: Long,
        category: String?,
        status: String?,
        keyword: String?,
        pageable: Pageable
    ): PageResponse<BoardPostDto.SimpleResponse> {
        val categoryNames = getCodeNameMap("BOARD_CATEGORY")
        val statusNames = getCodeNameMap("BOARD_STATUS")
        val page = boardPostRepository.searchActivePostSummaries(
            userSn = userSn,
            companySn = companySn,
            category = category.normalized(),
            status = status.normalized(),
            keyword = keyword.normalized(),
            pageable = pageable
        ).map { it.toSimpleResponse(categoryNames, statusNames) }

        return PageResponse.from(page)
    }

    @Transactional(readOnly = true)
    fun getCategoryCounts(companySn: Long): List<BoardPostDto.CategoryCountResponse> {
        val countsByCategory = boardPostRepository.countActivePostsByCategory(companySn)
            .associate { it.category to it.postCount }
        val totalCount = boardPostRepository.countByCompanySnAndUseYn(companySn, "Y")
        val categoryCounts = commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc("BOARD_CATEGORY", "Y")
            .map {
                BoardPostDto.CategoryCountResponse(
                    category = it.code,
                    categoryName = it.codeNm,
                    count = countsByCategory[it.code] ?: 0
                )
            }

        return listOf(
            BoardPostDto.CategoryCountResponse(
                category = "ALL",
                categoryName = "전체",
                count = totalCount
            )
        ) + categoryCounts
    }

    @Transactional(readOnly = true)
    fun getRecentNotices(companySn: Long, size: Int): List<BoardPostDto.RecentNoticeResponse> {
        return boardPostRepository.findByCompanySnAndCategoryAndUseYn(
            companySn = companySn,
            category = "NOTICE",
            useYn = "Y",
            pageable = PageRequest.of(
                0,
                size.coerceIn(1, 10),
                Sort.by(Sort.Direction.DESC, "createDt").and(Sort.by(Sort.Direction.DESC, "boardPostSn"))
            )
        ).content.map {
            BoardPostDto.RecentNoticeResponse(
                boardPostSn = it.boardPostSn!!,
                title = it.title,
                createDt = it.createDt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getSidebar(userSn: Long, companySn: Long, recentNoticeSize: Int, recentBookmarkSize: Int): BoardPostDto.SidebarResponse {
        val categoryNames = getCodeNameMap("BOARD_CATEGORY")
        val bookmarkCount = boardPostBookmarkRepository.countActiveBookmarks(userSn, companySn)
        val myActivity = boardPostRepository.findUserActivity(userSn, companySn)
        val activeUsers = boardPostRepository.findActiveUserActivities(companySn, PageRequest.of(0, 5)).map {
            BoardPostDto.ActiveUserResponse(
                userSn = it.userSn,
                userName = it.userName,
                avatarUrl = it.avatarUrl,
                postCount = it.postCount,
                commentCount = it.commentCount
            )
        }
        val recentBookmarks = boardPostBookmarkRepository.findRecentActiveBookmarks(
            userSn = userSn,
            companySn = companySn,
            pageable = PageRequest.of(0, recentBookmarkSize.coerceIn(1, 10))
        ).content.map { it.toRecentBookmarkResponse(categoryNames) }

        return BoardPostDto.SidebarResponse(
            categoryCounts = getCategoryCounts(companySn),
            recentNotices = getRecentNotices(companySn, recentNoticeSize),
            bookmarks = BoardPostDto.BookmarkSummaryResponse(
                count = bookmarkCount,
                recentPosts = recentBookmarks
            ),
            authoredPosts = BoardPostDto.AuthoredPostSummaryResponse(
                count = myActivity.postCount
            ),
            activeUsers = activeUsers,
            myActivity = BoardPostDto.MyActivityResponse(
                postCount = myActivity.postCount,
                commentCount = myActivity.commentCount
            )
        )
    }

    @Transactional
    fun getPost(userSn: Long, companySn: Long, boardPostSn: Long): BoardPostDto.Response {
        val updatedCount = boardPostRepository.increaseViewCount(companySn, boardPostSn)
        require(updatedCount > 0) { "Board post not found" }

        val post = boardPostRepository.findActivePostDetail(userSn, companySn, boardPostSn)
            ?: throw IllegalArgumentException("Board post not found")
        return post.toResponse(getCodeNameMap("BOARD_CATEGORY"), getCodeNameMap("BOARD_STATUS"))
    }

    @Transactional
    fun updatePost(
        userSn: Long,
        companySn: Long,
        boardPostSn: Long,
        request: BoardPostDto.UpdateRequest
    ): BoardPostDto.Response {
        validatePostValues(request.category, request.status, request.title, request.content)

        val post = getActivePost(companySn, boardPostSn)
        validatePostAuthor(post, userSn)
        post.category = request.category.trim()
        post.status = request.status.trim()
        post.title = request.title.trim()
        post.content = request.content.trim()

        return post.toResponse(userSn, getCodeNameMap("BOARD_CATEGORY"), getCodeNameMap("BOARD_STATUS"))
    }

    @Transactional
    fun deletePost(userSn: Long, companySn: Long, boardPostSn: Long) {
        val post = getActivePost(companySn, boardPostSn)
        validatePostAuthor(post, userSn)
        val postSn = post.boardPostSn!!
        post.useYn = "N"
        boardPostBookmarkRepository.deactivateByBoardPostSn(postSn)
        boardPostLikeRepository.deactivateByBoardPostSn(postSn)
        boardCommentLikeRepository.deactivateByBoardPostSn(postSn)
        boardCommentRepository.deactivateByBoardPostSn(postSn)
    }

    @Transactional
    fun likePost(userSn: Long, companySn: Long, boardPostSn: Long) {
        val post = getActivePost(companySn, boardPostSn)
        val postSn = post.boardPostSn!!
        val like = boardPostLikeRepository.findByBoardPostSnAndUserSn(postSn, userSn)

        if (like == null) {
            boardPostLikeRepository.save(BoardPostLike(boardPostSn = postSn, userSn = userSn))
        } else {
            like.useYn = "Y"
        }
    }

    @Transactional
    fun unlikePost(userSn: Long, companySn: Long, boardPostSn: Long) {
        val post = getActivePost(companySn, boardPostSn)
        boardPostLikeRepository.findByBoardPostSnAndUserSn(post.boardPostSn!!, userSn)?.useYn = "N"
    }

    @Transactional
    fun bookmarkPost(userSn: Long, companySn: Long, boardPostSn: Long) {
        val post = getActivePost(companySn, boardPostSn)
        val postSn = post.boardPostSn!!
        val bookmark = boardPostBookmarkRepository.findByBoardPostSnAndUserSn(postSn, userSn)

        if (bookmark == null) {
            boardPostBookmarkRepository.save(BoardPostBookmark(boardPostSn = postSn, userSn = userSn))
        } else {
            bookmark.useYn = "Y"
        }
    }

    @Transactional
    fun unbookmarkPost(userSn: Long, companySn: Long, boardPostSn: Long) {
        val post = getActivePost(companySn, boardPostSn)
        boardPostBookmarkRepository.findByBoardPostSnAndUserSn(post.boardPostSn!!, userSn)?.useYn = "N"
    }

    internal fun getActivePost(companySn: Long, boardPostSn: Long): BoardPost {
        return boardPostRepository.findByBoardPostSnAndCompanySnAndUseYn(boardPostSn, companySn, "Y")
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

    private fun validatePostAuthor(post: BoardPost, userSn: Long) {
        require(post.authorUserSn == userSn) { "Only the post author can modify this board post" }
    }

    private fun getCodeNameMap(codeGroup: String): Map<String, String> {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup, "Y")
            .associate { it.code to it.codeNm }
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun BoardPostSummaryProjection.toSimpleResponse(
        categoryNames: Map<String, String>,
        statusNames: Map<String, String>
    ) =
        BoardPostDto.SimpleResponse(
            boardPostSn = boardPostSn,
            authorUserSn = authorUserSn,
            authorName = authorName,
            category = category,
            categoryName = categoryNames[category],
            status = status,
            statusName = statusNames[status],
            title = title,
            content = content,
            viewCnt = viewCnt,
            commentCount = commentCount,
            likeCount = likeCount,
            likedByMe = likedByMe,
            bookmarkedByMe = bookmarkedByMe,
            createDt = createDt,
            updateDt = updateDt
        )

    private fun BoardPostDetailProjection.toResponse(
        categoryNames: Map<String, String>,
        statusNames: Map<String, String>
    ) =
        BoardPostDto.Response(
            boardPostSn = boardPostSn,
            authorUserSn = authorUserSn,
            authorName = authorName,
            category = category,
            categoryName = categoryNames[category],
            status = status,
            statusName = statusNames[status],
            title = title,
            content = content,
            viewCnt = viewCnt,
            commentCount = commentCount,
            likeCount = likeCount,
            likedByMe = likedByMe,
            bookmarkedByMe = bookmarkedByMe,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )

    private fun RecentBookmarkProjection.toRecentBookmarkResponse(categoryNames: Map<String, String>) =
        BoardPostDto.RecentBookmarkResponse(
            boardPostSn = boardPostSn,
            category = category,
            categoryName = categoryNames[category],
            title = title,
            createDt = createDt,
            bookmarkedDt = bookmarkedDt
        )

    private fun BoardPost.toResponse(
        userSn: Long,
        categoryNames: Map<String, String>,
        statusNames: Map<String, String>
    ) =
        BoardPostDto.Response(
            boardPostSn = boardPostSn!!,
            authorUserSn = authorUserSn,
            authorName = userRepository.findById(authorUserSn).orElse(null)?.userNm,
            category = category,
            categoryName = categoryNames[category],
            status = status,
            statusName = statusNames[status],
            title = title,
            content = content,
            viewCnt = viewCnt,
            commentCount = boardCommentRepository.countByBoardPostSnAndUseYn(boardPostSn!!, "Y"),
            likeCount = boardPostLikeRepository.countByBoardPostSnAndUseYn(boardPostSn!!, "Y"),
            likedByMe = boardPostLikeRepository.existsByBoardPostSnAndUserSnAndUseYn(boardPostSn!!, userSn, "Y"),
            bookmarkedByMe = boardPostBookmarkRepository.existsByBoardPostSnAndUserSnAndUseYn(
                boardPostSn!!,
                userSn,
                "Y"
            ),
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
