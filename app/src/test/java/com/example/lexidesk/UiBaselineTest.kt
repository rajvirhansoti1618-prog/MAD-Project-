package com.example.lexidesk

import com.example.lexidesk.ui.navigation.Screen
import com.example.lexidesk.ui.navigation.bottomNavItems
import com.example.lexidesk.ui.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Baseline test suite verifying navigation route integrity, parameter binding,
 * and theme mode enumerations prior to UI modernization.
 */
class UiBaselineTest {

    @Test
    fun bottomNavItems_containsExpectedScreens() {
        assertEquals(5, bottomNavItems.size)
        assertTrue(bottomNavItems.contains(Screen.Dashboard))
        assertTrue(bottomNavItems.contains(Screen.Clients))
        assertTrue(bottomNavItems.contains(Screen.Work))
        assertTrue(bottomNavItems.contains(Screen.Calendar))
        assertTrue(bottomNavItems.contains(Screen.More))
    }

    @Test
    fun clientDetailRoute_formatsCorrectly() {
        val route = Screen.ClientDetail.createRoute(42L)
        assertEquals("client_detail/42", route)
    }

    @Test
    fun caseDetailRoute_formatsCorrectly() {
        val route = Screen.CaseDetail.createRoute(108L)
        assertEquals("case_detail/108", route)
    }

    @Test
    fun addEditTaskRoute_withOptionalParameters() {
        val routeWithAll = Screen.AddEditTask.createRoute(taskId = 1L, clientId = 2L, caseId = 3L)
        assertEquals("add_edit_task?taskId=1&clientId=2&caseId=3", routeWithAll)

        val routeEmpty = Screen.AddEditTask.createRoute()
        assertEquals("add_edit_task", routeEmpty)
    }

    @Test
    fun themeModeEnum_containsExpectedValues() {
        val modes = ThemeMode.values().map { it.name }
        assertTrue(modes.contains("SYSTEM"))
        assertTrue(modes.contains("LIGHT"))
        assertTrue(modes.contains("DARK"))
    }
}
