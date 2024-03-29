package com.apiintegration.core.request;

import java.net.URI;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class UpdateServicesRequest {

	@NotBlank
	private Long serviceId;

	private String serviceName;

	private URI baseUrl;

	private URI baseUrlLive;

	private boolean isLive;

}