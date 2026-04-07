package com.hms.meenakshi.service;

import com.hms.meenakshi.dto.Expenses;
import com.hms.meenakshi.repository.ExpensesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpensesService {

    private final ExpensesRepository expensesRepository;

    public void saveExpense(Expenses expense){
        expensesRepository.save(expense);
    }

    public List<Expenses> findAllExpenses(String month){
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return expensesRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(start,end);
    }

    public void deleteExpense(String id){
        expensesRepository.deleteById(id);
    }
}
