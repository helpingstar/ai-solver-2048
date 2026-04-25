# Troubleshooting

## Common Issues

### Compose preview not rendering

**Problem**: @Preview functions show "Rendering problem"

**Solution**:
1. Check for missing theme wrapper: `AiSolver2048Theme { YourComposable() }`
2. Verify no ViewModel dependency in preview (use state-based preview)
3. Clean and rebuild project

### ProGuard/R8 stripping required classes

**Problem**: Release build crashes with missing class errors

**Solution**:
1. Add keep rules to `proguard-rules.pro`
2. Check `consumer-rules.pro` in library modules
3. Verify kotlinx.serialization rules are present

## Debug Tips

- **Timber Logging**: Enabled in debug builds, check Logcat with tag filter
