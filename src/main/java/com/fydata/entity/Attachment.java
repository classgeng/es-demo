package com.fydata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Attachment {

    private Date date;

    private String content_type;

    private String author;

    private String language;

    private String title;

    private String content;

    private Long content_length;

}
