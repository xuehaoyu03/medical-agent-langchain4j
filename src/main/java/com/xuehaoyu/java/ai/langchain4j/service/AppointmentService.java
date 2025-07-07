package com.xuehaoyu.java.ai.langchain4j.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuehaoyu.java.ai.langchain4j.entity.Appointment;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;

/**
 * @author :xuehaoyu
 * @date : 2025/7/7 15:16
 */

public interface AppointmentService extends IService<Appointment> {
    Appointment getOne(Appointment appointment);
}
