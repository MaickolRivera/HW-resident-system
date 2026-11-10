package com.hwresident.presentation.gui.presenter;

/** Modelo de vista de una persona externa (visitante o prestador de servicio). */
public record VisitorRow(String fullName, String documentNumber, String type, String company, String labor) {}
