package io.github.timely.timelyapi.home.service

import io.github.timely.timelyapi.board.service.BoardPostService
import io.github.timely.timelyapi.home.dto.HomeDashboardDto
import io.github.timely.timelyapi.project.service.ProjectService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeDashboardService(
    private val projectService: ProjectService,
    private val boardPostService: BoardPostService
) {

    @Transactional(readOnly = true)
    fun getDashboard(companySn: Long): HomeDashboardDto.Response {
        val projectStatusCounts = projectService.getStatusCounts(companySn)
        val boardCategoryCounts = boardPostService.getCategoryCounts(companySn)
        val recentNotices = boardPostService.getRecentNotices(companySn, RECENT_NOTICE_SIZE)

        return HomeDashboardDto.Response(
            projects = HomeDashboardDto.ProjectSummaryResponse(
                statusCounts = HomeDashboardDto.ProjectStatusCountsResponse(
                    totalCount = projectStatusCounts.totalCount,
                    inProgressCount = projectStatusCounts.inProgressCount,
                    completedCount = projectStatusCounts.completedCount,
                    onHoldCount = projectStatusCounts.onHoldCount
                )
            ),
            boards = HomeDashboardDto.BoardSummaryResponse(
                categoryCounts = boardCategoryCounts.map {
                    HomeDashboardDto.BoardCategoryCountResponse(
                        category = it.category,
                        categoryName = it.categoryName,
                        count = it.count
                    )
                },
                recentNotices = recentNotices.map {
                    HomeDashboardDto.RecentNoticeResponse(
                        boardPostSn = it.boardPostSn,
                        title = it.title,
                        createDt = it.createDt
                    )
                }
            )
        )
    }

    private companion object {
        const val RECENT_NOTICE_SIZE = 3
    }
}
