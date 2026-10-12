package io.nur.aiagent.agent

import java.util.Locale

/**
 * Creates a reviewable task plan only. This class never executes device actions,
 * calls a network, opens other apps, or grants permissions.
 */
class TaskPlanner {

    fun createPlan(rawRequest: String): TaskPlan {
        val request = rawRequest.trim()
        require(request.isNotEmpty()) { "আগে একটি কাজ লিখুন।" }
        require(request.length <= MAX_REQUEST_LENGTH) {
            "কাজের বিবরণ সর্বোচ্চ $MAX_REQUEST_LENGTH অক্ষরের মধ্যে রাখুন।"
        }

        val sensitiveMatch = findSensitiveIndicator(request)
        val approvalReason = sensitiveMatch?.let {
            "এই অনুরোধে বাইরের বা গুরুত্বপূর্ণ পরিবর্তনের সম্ভাবনা আছে (মিলেছে: \"$it\")। কোনো কাজ করার আগে পর্যালোচনা ও স্পষ্ট অনুমোদন দরকার।"
        }

        val steps = listOf(
            TaskStep(
                id = "understand",
                title = "কাজটি বুঝুন",
                description = "লক্ষ্য, প্রত্যাশিত ফলাফল এবং অনুরোধের সীমা স্পষ্ট করুন।"
            ),
            TaskStep(
                id = "scope",
                title = "টুল ও অনুমতি যাচাই করুন",
                description = "কোন তথ্য বা টুল দরকার তা নির্ধারণ করুন; অপ্রয়োজনীয় অনুমতি নেবেন না।"
            ),
            if (approvalReason != null) {
                TaskStep(
                    id = "approval",
                    title = "ব্যবহারকারীর অনুমতির জন্য অপেক্ষা করুন",
                    description = "অনুমোদন না পাওয়া পর্যন্ত কোনো বাহ্যিক বা গুরুত্বপূর্ণ কাজ করা যাবে না।"
                )
            } else {
                TaskStep(
                    id = "review",
                    title = "পরিকল্পনা পর্যালোচনা করুন",
                    description = "এই পরিকল্পনাটি যাচাই করুন। এই স্টার্টার সংস্করণ নিজে থেকে কোনো কাজ সম্পাদন করে না।"
                )
            },
            TaskStep(
                id = "verify",
                title = "ফলাফল যাচাইয়ের উপায় নির্ধারণ করুন",
                description = "পরে কাজ বাস্তবায়িত হলে প্রত্যাশিত ফলাফল কীভাবে পরীক্ষা হবে তা ঠিক করুন।"
            )
        )

        return TaskPlan(
            request = request,
            summary = if (approvalReason != null) {
                "এই কাজটি সংবেদনশীল হতে পারে; অনুমতি যাচাই করা দরকার।"
            } else {
                "একটি প্রাথমিক পরিকল্পনা তৈরি হয়েছে। এটি এখনো সম্পাদিত হয়নি।"
            },
            steps = steps,
            requiresApproval = approvalReason != null,
            approvalReason = approvalReason
        )
    }

    private fun findSensitiveIndicator(request: String): String? {
        val englishPatterns = listOf(
            Regex("""\b(delete|remove|erase|wipe|transfer|pay|payment|purchase|buy|send|submit|publish|post|share|install|uninstall)\b""", RegexOption.IGNORE_CASE),
            Regex("""\b(change|reset|update)\s+(my\s+)?(password|settings|account)\b""", RegexOption.IGNORE_CASE)
        )

        englishPatterns.forEach { pattern ->
            val match = pattern.find(request)
            if (match != null) return match.value
        }

        val banglaIndicators = listOf(
            "টাকা পাঠা",
            "পেমেন্ট",
            "মুছে ফেল",
            "মুছে দাও",
            "ডিলিট",
            "মেসেজ পাঠা",
            "বার্তা পাঠা",
            "ইমেইল পাঠা",
            "পোস্ট কর",
            "শেয়ার কর",
            "শেয়ার কর",
            "পাসওয়ার্ড",
            "পাসওয়ার্ড",
            "সেটিংস বদল",
            "কিনে দাও",
            "ইনস্টল কর",
            "আনইনস্টল"
        )
        val lowerRequest = request.lowercase(Locale.ROOT)
        return banglaIndicators.firstOrNull { lowerRequest.contains(it.lowercase(Locale.ROOT)) }
    }

    companion object {
        const val MAX_REQUEST_LENGTH = 1000
    }
}

data class TaskStep(
    val id: String,
    val title: String,
    val description: String
)

data class TaskPlan(
    val request: String,
    val summary: String,
    val steps: List<TaskStep>,
    val requiresApproval: Boolean,
    val approvalReason: String?
)
