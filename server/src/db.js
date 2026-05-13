import { createClient } from "@supabase/supabase-js";
import "dotenv/config";

const supabaseUrl = (process.env.SUPABASE_URL || "").trim();
const supabaseKey = (process.env.SUPABASE_SERVICE_ROLE_KEY || process.env.SUPABASE_ANON_KEY || "").trim();

const looksLikePlaceholder = (value) => {
  const normalized = (value || "").trim().toLowerCase();
  return (
    !normalized ||
    normalized.includes("project.supabase.co") ||
    normalized.includes("your_") ||
    normalized.includes("supabase-anon-key") ||
    normalized.includes("supabase-service-role-key")
  );
};

if (looksLikePlaceholder(supabaseUrl) || looksLikePlaceholder(supabaseKey)) {
  throw new Error(
    "Invalid Supabase configuration. Set SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY to real values; placeholder values like https://project.supabase.co or supabase-anon-key will cause login to fail.",
  );
}

export const db = createClient(supabaseUrl, supabaseKey);
