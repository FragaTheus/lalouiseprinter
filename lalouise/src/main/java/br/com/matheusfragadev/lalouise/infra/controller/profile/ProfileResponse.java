package br.com.matheusfragadev.lalouise.infra.controller.profile;

import java.util.UUID;

public sealed interface ProfileResponse permits AdminResponse, StaffResponse {
    UUID id();
    String nickname();
    String email();
    String role();
    boolean active();
}

