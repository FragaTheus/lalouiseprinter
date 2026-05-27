package br.com.matheusfragadev.lalouise.application.auth;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Conta bloqueada por 15 minutos após múltiplas tentativas de login incorretas");
    }
}
