package com.back.boundedContext.payout.in;

import com.back.boundedContext.payout.app.PayoutFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PayoutCollectItemsAndCompletePayoutsBatchJobConfig {
    private static final int CHUNK_SIZE = 10;

    private final PayoutFacade payoutFacade;

    public PayoutCollectItemsAndCompletePayoutsBatchJobConfig(PayoutFacade payoutFacade) {
        this.payoutFacade = payoutFacade;
    }

    @Bean
    public Job payoutCollectItemsAndCompletePayoutsJob(
            JobRepository jobRepository,
            Step payoutCollectItemsStep,
            Step payoutCompletePayouts
    ) {
        return new JobBuilder("payoutCollectItemsAndCompletePayoutsJob", jobRepository)
                .start(payoutCollectItemsStep)
                .next(payoutCompletePayouts)
                .build();
    }

    @Bean
    public Step payoutCollectItemsStep(JobRepository jobRepository) {
        return new StepBuilder("payoutCollectItemsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // candidate 데이터에서 실제로 payout item 데이터를 만드는 것
                    int processedCount = payoutFacade.collectPayoutItemsMore(CHUNK_SIZE).getData();
                    /**
                     * CHUNK_SIZE(10)개씩 처리, 끝날때까지(processedCount가 0이 될 때까지)
                     */
                    if (processedCount == 0) {
                        return RepeatStatus.FINISHED;
                    }

                    contribution.incrementWriteCount(processedCount);

                    return RepeatStatus.CONTINUABLE;
                })
                .build();
    }

    @Bean
    public Step payoutCompletePayouts(JobRepository jobRepository) {
        return new StepBuilder("payoutCompletePayouts", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 정산 지급을 하는 과정, 각자의 wallet에 넣엉줌
                    int processedCount = payoutFacade.completePayoutsMore(CHUNK_SIZE).getData();

                    if (processedCount == 0) {
                        return RepeatStatus.FINISHED;
                    }

                    contribution.incrementWriteCount(processedCount);

                    return RepeatStatus.CONTINUABLE;
                })
                .build();
    }
}