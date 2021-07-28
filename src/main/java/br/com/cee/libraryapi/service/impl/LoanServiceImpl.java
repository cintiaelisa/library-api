package br.com.cee.libraryapi.service.impl;

import br.com.cee.libraryapi.model.entity.Loan;
import br.com.cee.libraryapi.model.repository.LoanRepository;
import br.com.cee.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
