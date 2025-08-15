package com.gkmonk.pos.services.logs;

import com.gkmonk.pos.model.logs.TaskLogs;
import com.gkmonk.pos.repo.logs.TaskLogRepo;
import com.gkmonk.pos.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskLogsServiceImpl {

    private List<TaskLogs> taskLogsList;
    @Autowired
    private TaskLogRepo  taskLogRepo;

    public void saveTaskLog(TaskLogs taskLogs) {
        taskLogRepo.save(taskLogs);
    }

    public void addLogs(String taskName,String taskStatus, String metaData,String taskDate) {
        TaskLogs taskLogs = new TaskLogs();
        taskLogs.setTaskName(taskName);
        taskLogs.setStatus(taskStatus);
        taskLogs.setTaskDate(DateUtils.getTodaysDateInIST());
        taskLogs.setMetaData(metaData);
        if(taskLogsList == null) {
            taskLogsList = new ArrayList<>();
        }
        taskLogsList.add(taskLogs);
    }

    @Scheduled(fixedDelay = 10000)
    public void updateTaskLog(){
        if(taskLogsList != null && !taskLogsList.isEmpty()) {
            taskLogRepo.saveAll(taskLogsList);
            taskLogsList.clear();
        }
    }

    public Optional<List<TaskLogs>> getAllTasks() {
        return taskLogRepo.findAllLast30();
    }
}
