package com.xjhqre.admin.controller.monitor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.monitor.Server;

import io.swagger.annotations.Api;

/**
 * 服务器监控
 * 
 * @author xjhqre
 */
@RestController
@Api(value = "服务器监控", tags = "服务器监控")
@RequestMapping("/monitor/server")
public class ServerController extends BaseController {
    @PreAuthorize("@ss.hasPermission('monitor:server:list')")
    @GetMapping()
    public R<Server> getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.success(server);
    }
}
