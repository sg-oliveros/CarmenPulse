package com.example.carmenpulse.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object  SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://zkqfggxbjbsadewmdtzq.supabase.co",
        supabaseKey = "sb_publishable_UKNAcpaKWFm2U_hsb6wDmg_-SyBW4zr"
    ) {
        install(Postgrest)
        install(Auth)
    }
}