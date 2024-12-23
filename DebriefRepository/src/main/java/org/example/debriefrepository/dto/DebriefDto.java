package org.example.debriefrepository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebriefDto {
    private Long id;
    private String content;
    private UserDto user;
    private GroupDto group;
}
