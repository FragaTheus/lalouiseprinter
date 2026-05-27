package br.com.matheusfragadev.lalouise.infra.security.bruteforce;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Conta bloqueada por 15 minutos após múltiplas tentativas de login incorretas");
    }
}
