package com.mycoach.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAlunoNome() {
        Aluno aluno = new Aluno();
        String nome = "Teste Aluno";
        aluno.setNome(nome);
        assertEquals(nome, aluno.getNome());
    }

    @Test
    public void testAlunoEmail() {
        Aluno aluno = new Aluno();
        String email = "teste@exemplo.com";
        aluno.setEmail(email);
        assertEquals(email, aluno.getEmail());
    }

    @Test
    public void testAlunoSenha() {
        Aluno aluno = new Aluno();
        String senha = "123456";
        aluno.setSenha(senha);
        assertEquals(senha, aluno.getSenha());
    }

    @Test
    public void testAlunoId() {
        Aluno aluno = new Aluno();
        int id = 1;
        aluno.setId(id);
        assertEquals(id, aluno.getId());
    }
}