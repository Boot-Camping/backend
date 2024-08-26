package com.github.project3.entity.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailEntity {
    private String to;
    private String subject;
    private String message;
}
