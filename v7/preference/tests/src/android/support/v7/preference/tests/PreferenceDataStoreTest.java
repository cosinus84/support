/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v7.preference.tests;

import static junit.framework.TestCase.assertNotNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDataStore;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * Tests for {@link PreferenceDataStore} API.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class PreferenceDataStoreTest {

    private PreferenceWrapper mPreference;
    private PreferenceDataStore mDataStore;
    private PreferenceScreen mScreen;
    private PreferenceManager mManager;
    private SharedPreferences mSharedPref;

    private static final String KEY = "TestPrefKey";
    private static final String TEST_STR = "Test";
    private static final String TEST_DEFAULT_STR = "TestDefault";
    private static final String TEST_WRONG_STR = "TestFromSharedPref";

    @Before
    @UiThreadTest
    public void setup() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDataStore = mock(PreferenceDataStore.class);

        mManager = new PreferenceManager(context);
        mSharedPref = mManager.getSharedPreferences();
        mScreen = mManager.createPreferenceScreen(context);
        mPreference = new PreferenceWrapper(context);
        mPreference.setKey(KEY);

        // Make sure that the key is not present in SharedPreferences to ensure test
        // correctness.
        mManager.getSharedPreferences().edit().remove(KEY).commit();
    }

    @Test
    @UiThreadTest
    public void testPutStringWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putStringTestCommon();
    }

    @Test
    @UiThreadTest
    public void testPutStringWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putStringTestCommon();
    }

    private void putStringTestCommon() {
        mPreference.putString(TEST_STR);

        verify(mDataStore, atLeast(0)).getString(eq(KEY), anyString());
        verify(mDataStore, atLeastOnce()).putString(eq(KEY), anyString());
        verifyNoMoreInteractions(mDataStore);

        // Test that the value was NOT propagated to SharedPreferences.
        assertNull(mSharedPref.getString(KEY, null));
    }

    @Test
    @UiThreadTest
    public void testGetStringWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getString(TEST_STR);
        verify(mDataStore, atLeastOnce()).getString(eq(KEY), eq(TEST_STR));
    }

    @Test
    @UiThreadTest
    public void testGetStringWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getString(TEST_STR);
        verify(mDataStore, atLeastOnce()).getString(eq(KEY), eq(TEST_STR));
    }

    /**
     * This test makes sure that when a default value is set to a preference that has a data store
     * assigned that the default value is correctly propagated to
     * {@link Preference#onSetInitialValue(boolean, Object)} instead of passing a value from
     * {@link android.content.SharedPreferences}. We have this test only for String because the
     * implementation is not dependent on value type so this coverage should be fine.
     */
    @Test
    @UiThreadTest
    public void testDefaultStringValue() {
        mPreference.setPreferenceDataStore(mDataStore);
        mPreference.setDefaultValue(TEST_DEFAULT_STR);
        mSharedPref.edit().putString(KEY, TEST_WRONG_STR).commit();
        mScreen.addPreference(mPreference);
        mSharedPref.edit().remove(KEY).commit();
        assertEquals(TEST_DEFAULT_STR, mPreference.mDefaultValue);
    }

    @Test
    @UiThreadTest
    public void testPutIntWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putIntTestCommon();
    }

    @Test
    @UiThreadTest
    public void testPutIntWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putIntTestCommon();
    }

    private void putIntTestCommon() {
        mPreference.putInt(1);

        verify(mDataStore, atLeast(0)).getInt(eq(KEY), anyInt());
        verify(mDataStore, atLeastOnce()).putInt(eq(KEY), anyInt());
        verifyNoMoreInteractions(mDataStore);

        // Test that the value was NOT propagated to SharedPreferences.
        assertEquals(-1, mSharedPref.getInt(KEY, -1));
    }

    @Test
    @UiThreadTest
    public void testGetIntWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getInt(1);
        verify(mDataStore, atLeastOnce()).getInt(eq(KEY), eq(1));
    }

    @Test
    @UiThreadTest
    public void testGetIntWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getInt(1);
        verify(mDataStore, atLeastOnce()).getInt(eq(KEY), eq(1));
    }

    @Test
    @UiThreadTest
    public void testPutLongWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putLongTestCommon();
    }

    @Test
    @UiThreadTest
    public void testPutLongWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putLongTestCommon();
    }

    private void putLongTestCommon() {
        mPreference.putLong(1L);

        verify(mDataStore, atLeast(0)).getLong(eq(KEY), anyLong());
        verify(mDataStore, atLeastOnce()).putLong(eq(KEY), anyLong());
        verifyNoMoreInteractions(mDataStore);

        // Test that the value was NOT propagated to SharedPreferences.
        assertEquals(-1, mSharedPref.getLong(KEY, -1L));
    }

    @Test
    @UiThreadTest
    public void testGetLongWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getLong(1L);
        verify(mDataStore, atLeastOnce()).getLong(eq(KEY), eq(1L));
    }

    @Test
    @UiThreadTest
    public void testGetLongWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getLong(1L);
        verify(mDataStore, atLeastOnce()).getLong(eq(KEY), eq(1L));
    }

    @Test
    @UiThreadTest
    public void testPutFloatWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putFloatTestCommon();
    }

    @Test
    @UiThreadTest
    public void testPutFloatWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putFloatTestCommon();
    }

    private void putFloatTestCommon() {
        mPreference.putFloat(1f);

        verify(mDataStore, atLeast(0)).getFloat(eq(KEY), anyFloat());
        verify(mDataStore, atLeastOnce()).putFloat(eq(KEY), anyFloat());
        verifyNoMoreInteractions(mDataStore);

        // Test that the value was NOT propagated to SharedPreferences.
        assertEquals(-1, mSharedPref.getFloat(KEY, -1f), 0.1f /* epsilon */);
    }

    @Test
    @UiThreadTest
    public void testGetFloatWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getFloat(1f);
        verify(mDataStore, atLeastOnce()).getFloat(eq(KEY), eq(1f));
    }

    @Test
    @UiThreadTest
    public void testGetFloatWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getFloat(1f);
        verify(mDataStore, atLeastOnce()).getFloat(eq(KEY), eq(1f));
    }

    @Test
    @UiThreadTest
    public void testPutBooleanWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putBooleanTestCommon();
    }

    @Test
    @UiThreadTest
    public void testPutBooleanWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        putBooleanTestCommon();
    }

    private void putBooleanTestCommon() {
        mPreference.putBoolean(true);

        verify(mDataStore, atLeast(0)).getBoolean(eq(KEY), anyBoolean());
        verify(mDataStore, atLeastOnce()).putBoolean(eq(KEY), anyBoolean());
        verifyNoMoreInteractions(mDataStore);

        // Test that the value was NOT propagated to SharedPreferences.
        assertEquals(false, mSharedPref.getBoolean(KEY, false));
    }

    @Test
    @UiThreadTest
    public void testGetBooleanWithDataStoreOnPref() {
        mPreference.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getBoolean(true);
        verify(mDataStore, atLeastOnce()).getBoolean(eq(KEY), eq(true));
    }

    @Test
    @UiThreadTest
    public void testGetBooleanWithDataStoreOnMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        mScreen.addPreference(mPreference);
        mPreference.getBoolean(true);
        verify(mDataStore, atLeastOnce()).getBoolean(eq(KEY), eq(true));
    }

    @Test
    @UiThreadTest
    public void testDataStoresHierarchy() {
        mPreference.setPreferenceDataStore(mDataStore);
        PreferenceDataStore secondaryDataStore = mock(PreferenceDataStore.class,
                Mockito.CALLS_REAL_METHODS);
        mManager.setPreferenceDataStore(secondaryDataStore);
        mScreen.addPreference(mPreference);
        mPreference.putString(TEST_STR);

        // Check that the Preference returns the correct data store.
        assertEquals(mDataStore, mPreference.getPreferenceDataStore());

        // Check that the secondary data store assigned to the manager was NOT used.
        verifyZeroInteractions(secondaryDataStore);

        // Check that the primary data store assigned directly to the preference was used.
        verify(mDataStore, atLeast(0)).getString(eq(KEY), anyString());
    }

    /**
     * When {@link PreferenceDataStore} is NOT assigned, the getter for SharedPreferences must not
     * return null for Preference.
     */
    @Test
    @UiThreadTest
    public void testSharedPrefNotNullIfNoDS() {
        mScreen.addPreference(mPreference);
        assertNotNull(mPreference.getSharedPreferences());
    }

    /**
     * When {@link PreferenceDataStore} is NOT assigned, the getter for SharedPreferences must not
     * return null for PreferenceManager.
     */
    @Test
    @UiThreadTest
    public void testSharedPrefNotNullIfNoDSMgr() {
        assertNotNull(mManager.getSharedPreferences());
    }

    /**
     * When {@link PreferenceDataStore} is assigned, the getter for SharedPreferences has to return
     * null for Preference.
     */
    @Test
    @UiThreadTest
    public void testSharedPrefNullIfWithDS() {
        mScreen.addPreference(mPreference);
        mPreference.setPreferenceDataStore(mDataStore);
        assertNull(mPreference.getSharedPreferences());
    }

    /**
     * When {@link PreferenceDataStore} is assigned, the getter for SharedPreferences has to return
     * null for PreferenceManager.
     */
    @Test
    @UiThreadTest
    public void testSharedPrefNullIfWithDSMgr() {
        mManager.setPreferenceDataStore(mDataStore);
        assertNull(mManager.getSharedPreferences());
    }

    /**
     * Wrapper to allow to easily call protected methods.
     */
    private static class PreferenceWrapper extends Preference {

        Object mDefaultValue;

        PreferenceWrapper(Context context) {
            super(context);
        }

        void putString(String value) {
            persistString(value);
        }

        String getString(String defaultValue) {
            return getPersistedString(defaultValue);
        }

        void putInt(int value) {
            persistInt(value);
        }

        int getInt(int defaultValue) {
            return getPersistedInt(defaultValue);
        }

        void putLong(long value) {
            persistLong(value);
        }

        long getLong(long defaultValue) {
            return getPersistedLong(defaultValue);
        }

        void putFloat(float value) {
            persistFloat(value);
        }

        float getFloat(float defaultValue) {
            return getPersistedFloat(defaultValue);
        }

        void putBoolean(boolean value) {
            persistBoolean(value);
        }

        boolean getBoolean(boolean defaultValue) {
            return getPersistedBoolean(defaultValue);
        }

        @Override
        protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
            this.mDefaultValue = defaultValue;
            super.onSetInitialValue(restorePersistedValue, defaultValue);
        }
    }
}