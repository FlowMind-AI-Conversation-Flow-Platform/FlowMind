package com.flowmind.dispatch;

import com.flowmind.dispatch.model.DispatchRequest;
import com.flowmind.dispatch.model.DispatchResponse;
import com.flowmind.dispatch.service.DispatchService;
import com.flowmind.dispatch.service.DispatchTelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

  private final DispatchService dispatchService;

  public DispatchController(DispatchService dispatchService) {
    this.dispatchService = dispatchService;
  }

  @PostMapping
  public ResponseEntity<DispatchResponse> dispatch(@RequestBody DispatchRequest request) {
    return ResponseEntity.ok(dispatchService.dispatch(request));
  }

  @GetMapping("/metrics")
  public ResponseEntity<DispatchTelemetryService.MetricsSnapshot> metrics() {
    return ResponseEntity.ok(dispatchService.metrics());
  }
}
