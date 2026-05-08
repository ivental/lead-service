package ru.mentee.power.crm.leadservice.adapter.in.rest;

import lombok.Data;

@Data
public class LeadUpdateRequest {
  private String title;
  private String description;
}
