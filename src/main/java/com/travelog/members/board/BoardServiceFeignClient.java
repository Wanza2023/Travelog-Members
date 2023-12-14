package com.travelog.members.board;

import com.travelog.members.dto.board.BoardDto;
import com.travelog.members.dto.board.BoardTotalViewsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "board")
public interface BoardServiceFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/board/bookmark", consumes = "application/json")
    List<BoardDto> getBoards(@RequestBody List<Long> boardIds);

    @GetMapping(value = "/board/views")
    List<BoardTotalViewsDto> getTotalViews();
}
