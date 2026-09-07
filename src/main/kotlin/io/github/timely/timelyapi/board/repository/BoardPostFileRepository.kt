package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPostFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardPostFileRepository : JpaRepository<BoardPostFile, Long> {

    fun findByBoardPostSnAndUseYnOrderByBoardPostFileSnAsc(
        boardPostSn: Long,
        useYn: String
    ): List<BoardPostFile>
}
