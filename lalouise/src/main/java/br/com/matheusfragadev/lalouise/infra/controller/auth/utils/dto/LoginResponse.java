package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

public sealed interface LoginResponse permits AdminLoginResponse, ManagerLoginResponse, StaffLoginResponse {
    String id();
    String nickname();
    String email();
    String role();
}
