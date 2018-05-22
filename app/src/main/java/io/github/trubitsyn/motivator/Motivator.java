package io.github.trubitsyn.motivator;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.github.trubitsyn.motivator.model.Preferences;
import io.github.trubitsyn.motivator.model.Repository;
import io.github.trubitsyn.motivator.model.SqliteTaskRepository;
import io.github.trubitsyn.motivator.model.Task;
import io.github.trubitsyn.motivator.model.TaskDbHelper;

public class Motivator extends Application {
    private TaskDbHelper taskDbHelper;
    private Repository<Task> taskRepository;
    private Preferences preferences;
    private Scheduler defaultScheduler;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        AndroidThreeTen.init(this);
        defaultScheduler = Schedulers.io();
        taskDbHelper = new TaskDbHelper(this);
        taskRepository = new SqliteTaskRepository(taskDbHelper);
        preferences = new Preferences(this);
    }

    @Override
    public void onTerminate() {
        taskRepository.destroy();
        taskDbHelper.close();
        preferences = null;
        super.onTerminate();
    }

    public static Motivator get(Context context) {
        return (Motivator) context.getApplicationContext();
    }

    public Repository<Task> getTaskRepository() {
        return taskRepository;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    @VisibleForTesting
    public void setTaskRepository(Repository<Task> repository) {
        this.taskRepository = repository;
    }

    public Scheduler getDefaultScheduler() {
        return defaultScheduler;
    }

    @VisibleForTesting
    public void setDefaultScheduler(Scheduler defaultScheduler) {
        this.defaultScheduler = defaultScheduler;
    }
}