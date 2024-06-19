package com.javeriana.component.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SyncRequest {
    private List<CoursesRequest> coursesRequests;
}
