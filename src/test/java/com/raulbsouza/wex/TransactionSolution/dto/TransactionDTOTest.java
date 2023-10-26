package com.raulbsouza.wex.TransactionSolution.dto;

import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionDTOTest {

    @Test
    void shouldConvertDtoToEntity() {
        TransactionDTO dto = TransactionDTO.builder()
                .id(null)
                .description("Transaction 1")
                .amount(TestUtils.randomAmount())
                .date(LocalDate.now()).build();

        Transaction entity = dto.toEntity();

        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getDate(), entity.getDate());
    }

    @Test
    void shouldConvertEntityToDto() {
        Transaction entity = Transaction.builder()
                .id(UUID.randomUUID())
                .description("Transaction 1")
                .amount(TestUtils.randomAmount())
                .date(LocalDate.now()).build();

        TransactionDTO dto = new TransactionDTO().toDTO(entity);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getDate(), entity.getDate());
    }

}
