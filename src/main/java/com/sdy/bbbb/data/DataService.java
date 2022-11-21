package com.sdy.bbbb.data;

import com.sdy.bbbb.data.dataDto.*;
import com.sdy.bbbb.data.entity.JamOfWeek;
import com.sdy.bbbb.dto.response.GlobalResponseDto;
import com.sdy.bbbb.entity.Gu;
import com.sdy.bbbb.exception.CustomException;
import com.sdy.bbbb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;
    private final JamRepository jamRepository;



    // 데이터 1 - 주말 데이터 저장 로직
    @Scheduled(cron = "0 0 1 * * MON")
    @Transactional
    public void saveWeekendData() {
        //새 주말 데이터
        List<JamDto> jamDtos = dataRepository.getJamWeekendFromDb();
        // 오래된 주말 데이터
        List<JamOfWeek> oldList = jamRepository.findByIsWeekend(true);
        // 새 데이터 업데이트
        for (int i = 0; i < jamDtos.size(); i++) {
            oldList.get(i).update(jamDtos.get(i));
        }

        log.info("주말 데이터 저장완료");
    }

    // 데이터 1 - 주중 데이터 저장 로직
    @Transactional
    @Scheduled(cron = "0 0 1 * * SAT")
    public void saveWeekdayData() {
        // 새 주중 데이터
        List<JamDto> jamWeekdays = dataRepository.getJamWeekDayFromDb();
        // 오래된 주중 데이터
        List<JamOfWeek> oldList = jamRepository.findByIsWeekend(false);
        // 새 데이터 업데이트
        for (int i = 0; i < oldList.size(); i++) {
            oldList.get(i).update(jamWeekdays.get(i));
        }

        log.info("주중 데이터 저장완료");
    }




    // 데이터 조회
    @Transactional(readOnly = true)
    public GlobalResponseDto<DataResponseDto> getMainInfo() {

        // data 1
        List<JamOfWeek> jamList = jamRepository.findAll();
        List<JamTop5Dto> jamDtoList = new ArrayList<>();
        for(JamOfWeek jam : jamList){
            jamDtoList.add(new JamTop5Dto(jam));
        }

        // data 2
        List<PopulationDto> popList = dataRepository.getPopulationFromDb();
        List<PopulationChangesDto> dtoList = new ArrayList<>();
        for (PopulationDto pop : popList) {
            dtoList.add(new PopulationChangesDto(pop));
        }

        return GlobalResponseDto.ok("조회 성공", new DataResponseDto(jamDtoList, dtoList));
    }


    // 구별 데이터 조회
    public GlobalResponseDto<BaseGuInfoDto> getGuInformation(String gu) {

        //구valid 해야함(준비중)

        List<GuBaseInfo> guBaseInfoList = dataRepository.getGuBaseInfo(gu);

        GuBaseInfo guBaseInfos;
        if (guBaseInfoList.size() != 0) {
            guBaseInfos = guBaseInfoList.get(0);
        }else {
            throw new CustomException(ErrorCode.NotReadyForData);
        }

        List<SpotInfoDto> spotInfoDtoList = new ArrayList<>();
        for(GuBaseInfo guBaseInfo : guBaseInfoList){
            spotInfoDtoList.add(new SpotInfoDto(guBaseInfo));
        }


        List<SpotCalculated> spotCalculateds = dataRepository.getGuInfo(gu);
        List<SpotCalculatedDto> spotCalculatedDtoList = new ArrayList<>();
        for(SpotCalculated spotData: spotCalculateds) {
            spotCalculatedDtoList.add(new SpotCalculatedDto(spotData));
        }

        return GlobalResponseDto.ok("조회 성공", new BaseGuInfoDto(guBaseInfos, spotInfoDtoList, spotCalculatedDtoList));
    }
}

