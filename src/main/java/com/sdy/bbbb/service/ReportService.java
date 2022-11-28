package com.sdy.bbbb.service;

import com.sdy.bbbb.dto.request.ReportRequestDto;
import com.sdy.bbbb.dto.response.GlobalResponseDto;
import com.sdy.bbbb.entity.Account;
import com.sdy.bbbb.entity.Comment;
import com.sdy.bbbb.entity.Post;
import com.sdy.bbbb.entity.Report;
import com.sdy.bbbb.exception.CustomException;
import com.sdy.bbbb.exception.ErrorCode;
import com.sdy.bbbb.repository.AccountRepository;
import com.sdy.bbbb.repository.CommentRepository;
import com.sdy.bbbb.repository.PostRepository;
import com.sdy.bbbb.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public GlobalResponseDto<?> report(Account account, ReportRequestDto reportRequestDto){
        Long level = reportRequestDto.getLevel();
        Long reporterId = reportRequestDto.getReporterId();
        Long reportedId = reportRequestDto.getReportedId();

        if (reportRepository.existsByLevelAndReporterIdAndReportedId(level, reporterId, reportedId)){
            throw new CustomException(ErrorCode.AlreadyReported);
        }

        Report report = new Report(reportRequestDto);

        reportRepository.save(report);

        if(level.equals(1L)){
            Account account1 = accountRepository.findById(reportedId).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundUser));
            if(account1.getReportedCount() > 9) {
                account1.setAccountName("부적절한닉네임");
                reportRepository.deleteAllByLevelAndReportedId(level, reportedId);
            }else {
                account1.setReportedCount(account1.getReportedCount() + 1);
            }

        }else if(level.equals(2L)){
            Post post = postRepository.findById(report.getReportedId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NotFoundPost));
            if(post.getReportedCount() > 9) {
                post.setHide(true);
                postRepository.save(post);
            } else {
                post.setReportedCount(post.getReportedCount() + 1);
            }

        }else if (level.equals(3L)) {
            Comment comment = commentRepository.findById(report.getReporterId()).orElseThrow(
                    ()-> new CustomException(ErrorCode.NotFoundComment));
            if(comment.getReportedCount() > 9) {
                comment.setHide(true);
                commentRepository.save(comment);
            } else {
                comment.setReportedCount(comment.getReportedCount() + 1);
            }
        }

        return GlobalResponseDto.ok("신고 완료", reportRequestDto);

    }

}
