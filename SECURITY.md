# Security Policy

## Baseline

- No API keys, access tokens, passwords, or private user data in Git.
- Do not put provider API keys in Android resources, BuildConfig, assets, or the APK.
- Use a server-side proxy or a trusted backend secret store for model credentials.
- Request Android permissions only when a feature needs them and explain the reason in the UI.
- Ask for explicit confirmation before consequential external actions.
- Provide a visible stop/cancel control and bounded execution time, tool count, and spending limits.
- Treat model output, remote pages, and imported documents as untrusted data.
- Keep logs minimal and redact secrets. Prefer user-visible audit entries over hidden background behavior.

## Reporting

Please open a private security advisory or contact the repository owner for sensitive findings. Avoid posting secrets or exploitable details in public issues.

## Scope

This is an early prototype, not a security-audited product. Do not use it for unattended high-impact decisions or unrestricted device automation.
