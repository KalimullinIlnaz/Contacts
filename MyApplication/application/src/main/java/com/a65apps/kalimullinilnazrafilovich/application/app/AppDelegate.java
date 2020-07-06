package com.a65apps.kalimullinilnazrafilovich.application.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.di.interfaces.AppContainer;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.di.interfaces.HasAppContainer;


public class AppDelegate extends Application implements HasAppContainer {

    @Nullable
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initDependencies();
    }

    private void initDependencies() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @NonNull
    @Override
    public AppContainer appContainer() {
        if (appComponent == null) {
            initDependencies();
        }
        return appComponent;
    }
}
