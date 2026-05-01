package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response;

import java.util.UUID;

public sealed interface ProfileResponse permits AdminResponse, StaffResponse {
    UUID id();
    String nickname();
    String email();
    String role();
    boolean active();
}

