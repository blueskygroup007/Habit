package com.bluesky.habit.util;

import android.content.Context;

import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.data.source.local.HabitsLocalDataSource;
import com.bluesky.habit.data.source.local.RHDatabase;
import com.bluesky.habit.data.source.remote.HabitsRemoteDataSource;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/24
 * Description:
 */
public class Injection {

    public static HabitsRepository provideTasksRepository(Context context) {
        checkNotNull(context);

        RHDatabase database = RHDatabase.getInstance(context);
        return HabitsRepository.getInstance(HabitsRemoteDataSource.getInstance(),
                HabitsLocalDataSource.getInstance(database.habitDao(), new AppExecutors()));
    }
}
