package io.nur.aiagent.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskPlannerTest {
    private val planner = TaskPlanner()

    @Test
    fun normalRequestCreatesReviewablePlanWithoutApprovalFlag() {
        val plan = planner.createPlan("আমার পড়াশোনার জন্য একটি সাপ্তাহিক রুটিন তৈরি করো")

        assertEquals("আমার পড়াশোনার জন্য একটি সাপ্তাহিক রুটিন তৈরি করো", plan.request)
        assertFalse(plan.requiresApproval)
        assertEquals(null, plan.approvalReason)
        assertEquals(listOf("understand", "scope", "review", "verify"), plan.steps.map { it.id })
        assertTrue(plan.steps.isNotEmpty())
    }

    @Test
    fun sendingMessageRequiresApproval() {
        val plan = planner.createPlan("send a message to my friend")

        assertTrue(plan.requiresApproval)
        assertNotNull(plan.approvalReason)
        assertTrue(plan.steps.any { it.id == "approval" })
    }

    @Test
    fun destructiveActionRequiresApproval() {
        val plan = planner.createPlan("delete my old files")

        assertTrue(plan.requiresApproval)
        assertTrue(plan.summary.contains("অনুমতি"))
    }

    @Test
    fun banglaPaymentAndMessageRequestsRequireApproval() {
        val paymentPlan = planner.createPlan("আমার বন্ধুকে টাকা পাঠাও")
        val messagePlan = planner.createPlan("এই মেসেজটি পাঠিয়ে দাও")

        assertTrue(paymentPlan.requiresApproval)
        assertTrue(messagePlan.requiresApproval)
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankRequestIsRejected() {
        planner.createPlan("   ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun oversizedRequestIsRejected() {
        planner.createPlan("a".repeat(TaskPlanner.MAX_REQUEST_LENGTH + 1))
    }

    @Test
    fun requestIsTrimmedBeforePlanning() {
        val plan = planner.createPlan("   open my study notes   ")

        assertEquals("open my study notes", plan.request)
    }
}
