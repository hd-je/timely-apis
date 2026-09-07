package io.github.timely.timelyapi.home.service

import io.github.timely.timelyapi.board.service.BoardPostService
import io.github.timely.timelyapi.home.dto.HomeDashboardDto
import io.github.timely.timelyapi.project.repository.ProjectUpdateRepository
import io.github.timely.timelyapi.project.service.ProjectService
import io.github.timely.timelyapi.schedule.service.ScheduleService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeDashboardService(
    private val projectService: ProjectService,
    private val boardPostService: BoardPostService,
    private val scheduleService: ScheduleService,
    private val projectUpdateRepository: ProjectUpdateRepository
) {

    @Transactional(readOnly = true)
    fun getDashboard(userSn: Long, companySn: Long, projectSize: Int, activitySize: Int): HomeDashboardDto.Response {
        val projectStatusCounts = projectService.getStatusCounts(companySn)
        val boardCategoryCounts = boardPostService.getCategoryCounts(companySn)
        val recentNotices = boardPostService.getRecentNotices(companySn, RECENT_NOTICE_SIZE)
        val assignedProjects = projectService.searchAssignedProjects(
            companySn = companySn,
            userSn = userSn,
            status = null,
            keyword = null,
            pageable = PageRequest.of(
                0,
                projectSize.coerceIn(1, MAX_HOME_LIST_SIZE),
                Sort.by(Sort.Direction.DESC, "createDt")
            )
        )
        val normalizedActivitySize = activitySize.coerceIn(1, MAX_HOME_LIST_SIZE)
        val recentBoardPosts = boardPostService.searchPosts(
            userSn = userSn,
            companySn = companySn,
            category = null,
            status = null,
            keyword = null,
            pageable = PageRequest.of(0, normalizedActivitySize, Sort.by(Sort.Direction.DESC, "createDt"))
        ).content.map {
            HomeDashboardDto.RecentActivityResponse(
                activityType = "BOARD_POST",
                targetSn = it.boardPostSn,
                projectSn = null,
                projectNm = null,
                authorUserSn = it.authorUserSn,
                authorName = it.authorName,
                title = it.title,
                content = it.content,
                createDt = it.createDt
            )
        }
        val recentProjectUpdates = projectUpdateRepository.findRecentAssignedProjectUpdates(
            companySn = companySn,
            userSn = userSn,
            pageable = PageRequest.of(0, normalizedActivitySize, Sort.by(Sort.Direction.DESC, "createDt"))
        ).content.map {
            HomeDashboardDto.RecentActivityResponse(
                activityType = "PROJECT_UPDATE",
                targetSn = it.projectUpdateSn,
                projectSn = it.projectSn,
                projectNm = it.projectNm,
                authorUserSn = it.authorUserSn,
                authorName = it.authorName,
                title = it.title,
                content = it.content,
                createDt = it.createDt
            )
        }

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
            ),
            assignedProjects = HomeDashboardDto.AssignedProjectSummaryResponse(
                totalCount = assignedProjects.totalElements,
                projects = assignedProjects.content
            ),
            upcomingSchedules = scheduleService.searchUpcomingSchedules(companySn, userSn),
            recentActivities = (recentBoardPosts + recentProjectUpdates)
                .sortedWith(
                    compareByDescending<HomeDashboardDto.RecentActivityResponse> { it.createDt }
                        .thenByDescending { it.targetSn }
                )
                .take(normalizedActivitySize)
        )
    }

    private companion object {
        const val RECENT_NOTICE_SIZE = 3
        const val MAX_HOME_LIST_SIZE = 10
    }
}
