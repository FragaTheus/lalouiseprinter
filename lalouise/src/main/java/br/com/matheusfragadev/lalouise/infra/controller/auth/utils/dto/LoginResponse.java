package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

public sealed interface LoginResponse permits AdminLoginResponse, ManagerLoginResponse {
    String id();
    String nickname();
    String email();
    String role();
}
