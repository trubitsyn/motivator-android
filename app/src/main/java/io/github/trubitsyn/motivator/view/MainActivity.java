package io.github.trubitsyn.motivator.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trubitsyn.motivator.Motivator;
import io.github.trubitsyn.motivator.R;
import io.github.trubitsyn.motivator.model.Preferences;
import io.github.trubitsyn.motivator.model.Task;
import io.github.trubitsyn.motivator.presenter.MainPresenter;
import io.github.trubitsyn.motivator.view.intro.IntroActivity;

public class MainActivity extends AppCompatActivity implements DatabaseConnectionCallback, EditCallback, MainView {
    public static final int INTRO_REQUEST_CODE = 0;
    public static final int TASK_NEW_REQUEST_CODE = 1;
    public static final int TASK_EDIT_REQUEST_CODE = 2;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyState) TextView emptyState;;
    private TaskAdapter adapter;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean introShown = Motivator.get(this).getPreferences().isIntroShown();

        if (!introShown) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivityForResult(intent, INTRO_REQUEST_CODE);
        } else {
            setupView();
        }
    }

    private void setupView() {
        presenter = new MainPresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView(recyclerView);

        presenter.loadTasks();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this, OrientationHelper.VERTICAL);
        adapter = new TaskAdapter(this, this, presenter);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        ItemTouchHelperCustomCallback callback = new ItemTouchHelperCustomCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(this, TaskActivity.class);
            startActivityForResult(intent, TASK_NEW_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (adapter != null) {
            adapter.invalidateOptionsViews();
        }

        if (requestCode == TASK_NEW_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Task task = data.getParcelableExtra(TaskActivity.INTENT_PARCELABLE_TASK);
                presenter.onNewTaskReceived(task);
                presenter.loadTasks();
            }
        } else if (requestCode == TASK_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Task task = data.getParcelableExtra(TaskActivity.INTENT_PARCELABLE_TASK);
                presenter.onTaskEdited(task);
                presenter.loadTasks();
            }
        } else if (requestCode == INTRO_REQUEST_CODE && resultCode == RESULT_OK) {
            Preferences preferences = Motivator.get(MainActivity.this).getPreferences();
            preferences.setIntroShown(true);
            setupView();
        }
    }

    @Override
    public void showTasks(List<Task> tasks) {
        adapter.setTasks(tasks);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEdit(Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.INTENT_PARCELABLE_TASK, task);
        startActivityForResult(intent, TASK_EDIT_REQUEST_CODE);
    }

    @Override
    public void removeItem(Task task) {
        presenter.onDeleteButtonClicked(task);
        presenter.loadTasks();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}