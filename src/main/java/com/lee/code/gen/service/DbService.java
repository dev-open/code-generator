package com.lee.code.gen.service;

import com.lee.code.gen.dto.GetTableRequestDto;
import com.lee.code.gen.dto.GetTableEntityResponseDto;
import com.lee.code.gen.dto.GetTableResponseDto;

import java.util.List;

public interface DbService {

    List<GetTableEntityResponseDto> queryTableWithColumns(String tableName, String accessToken);

    List<GetTableResponseDto> queryTable(GetTableRequestDto requestDto);
}
