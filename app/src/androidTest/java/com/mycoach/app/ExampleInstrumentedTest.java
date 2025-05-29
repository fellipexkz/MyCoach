package com.mycoach.app;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.mycoach.app", appContext.getPackageName());
    }

    @Test
    public void testLaunchDetalhesAlunoActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetalhesAlunoActivity.class);
        int testAlunoId = 1;
        intent.putExtra("aluno_id", testAlunoId);
        try (androidx.test.core.app.ActivityScenario<DetalhesAlunoActivity> scenario = androidx.test.core.app.ActivityScenario.launch(intent)) {
            onView(withId(R.id.studentNameTextView))
                    .check(matches(isDisplayed()));

            scenario.onActivity(activity -> {
            });
        }
    }
}