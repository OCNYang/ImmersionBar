# Android 15 Edge-to-Edge Usage Examples

## Basic Usage (Backward Compatible)

Your existing code continues to work without any changes:

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This works on all Android versions including Android 15
        ImmersionBar.with(this)
            .statusBarColor(R.color.colorPrimary)
            .navigationBarColor(R.color.colorPrimary)
            .statusBarDarkFont(true)
            .init();
    }
}
```

## Android 15+ Edge-to-Edge Mode

### 1. Handling System Insets

The recommended approach for Android 15+ is to listen to insets changes:

```java
public class EdgeToEdgeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View contentView = findViewById(R.id.content);

        ImmersionBar.with(this)
            .statusBarColor(Color.TRANSPARENT)
            .navigationBarColor(Color.TRANSPARENT)
            .statusBarDarkFont(true)
            .setOnInsetsChangeListener((top, bottom, left, right) -> {
                // Adjust your layout based on system bar insets
                contentView.setPadding(left, top, right, bottom);
            })
            .init();
    }
}
```

### 2. Kotlin DSL Style

```kotlin
class EdgeToEdgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contentView = findViewById<View>(R.id.content)

        immersionBar {
            statusBarColor(Color.TRANSPARENT)
            navigationBarColor(Color.TRANSPARENT)
            statusBarDarkFont(true)
            setOnInsetsChangeListener { top, bottom, left, right ->
                contentView.updatePadding(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom
                )
            }
        }
    }
}
```

### 3. Partial Insets (Status Bar Only)

```java
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)
    .statusBarDarkFont(true)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        // Only apply top padding for status bar
        findViewById(R.id.toolbar).setPadding(0, top, 0, 0);
    })
    .init();
```

### 4. RecyclerView with Edge-to-Edge

```java
public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        ImmersionBar.with(this)
            .statusBarColor(Color.TRANSPARENT)
            .navigationBarColor(Color.TRANSPARENT)
            .statusBarDarkFont(true)
            .setOnInsetsChangeListener((top, bottom, left, right) -> {
                // Apply insets as padding to RecyclerView
                recyclerView.setPadding(left, top, right, bottom);
                // Important: set clipToPadding to false for smooth scrolling
                recyclerView.setClipToPadding(false);
            })
            .init();
    }
}
```

### 5. Fragment Usage

```java
public class MyFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImmersionBar.with(this)
            .statusBarColor(Color.TRANSPARENT)
            .setOnInsetsChangeListener((top, bottom, left, right) -> {
                view.setPadding(left, top, right, bottom);
            })
            .init();
    }
}
```

Kotlin version:
```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        immersionBar {
            statusBarColor(Color.TRANSPARENT)
            setOnInsetsChangeListener { top, bottom, left, right ->
                view.updatePadding(left, top, right, bottom)
            }
        }
    }
}
```

## Version Detection and Debugging

### 1. Check Android Version

```kotlin
import com.gyf.immersionbar.ktx.isAndroid15OrAbove
import com.gyf.immersionbar.ktx.versionInfo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAndroid15OrAbove) {
            Log.d("ImmersionBar", "Running on Android 15+")
            Log.d("ImmersionBar", "Version info: $versionInfo")
            // Use Android 15 specific features
        }
    }
}
```

Java version:
```java
import com.gyf.immersionbar.VersionAdapter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VersionAdapter.isAndroid15OrAbove()) {
            Log.d("ImmersionBar", "Running on Android 15+");
            Log.d("ImmersionBar", "Version info: " + VersionAdapter.getVersionInfo());
        }
    }
}
```

### 2. Enable Debug Logging

```java
ImmersionBar.with(this)
    .debugPrintVersionInfo(true)  // Enable debug logging
    .statusBarColor(R.color.colorPrimary)
    .init();

// Check logcat for output like:
// D/ImmersionBar: Android 15+ Edge-to-Edge mode: Android 15.0 (API 35) - WindowInsetsController + Edge-to-Edge (enforced)
```

### 3. Force Edge-to-Edge for Testing (Android 11-14)

**WARNING**: This is for testing purposes only! Do not use in production.

```java
ImmersionBar.with(this)
    .debugForceEdgeToEdge(true)  // Force Edge-to-Edge on Android 11+
    .debugPrintVersionInfo(true)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        Log.d("Insets", String.format("top=%d, bottom=%d, left=%d, right=%d",
                                      top, bottom, left, right));
    })
    .init();
```

## Advanced Use Cases

### 1. Disable Edge-to-Edge (Use Legacy Mode on Android 15)

```java
ImmersionBar.with(this)
    .edgeToEdgeEnabled(false)  // Disable Edge-to-Edge even on Android 15
    .statusBarColor(R.color.colorPrimary)
    .navigationBarColor(R.color.colorPrimary)
    .init();
```

### 2. Dynamic Insets Handling

```java
public class DynamicActivity extends AppCompatActivity {
    private View toolbar;
    private View bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);

        ImmersionBar.with(this)
            .statusBarColor(Color.TRANSPARENT)
            .navigationBarColor(Color.TRANSPARENT)
            .statusBarDarkFont(true)
            .setOnInsetsChangeListener(this::handleInsets)
            .init();
    }

    private void handleInsets(int top, int bottom, int left, int right) {
        // Apply top inset to toolbar
        ViewGroup.MarginLayoutParams toolbarParams =
            (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        toolbarParams.topMargin = top;
        toolbar.setLayoutParams(toolbarParams);

        // Apply bottom inset to bottom navigation
        ViewGroup.MarginLayoutParams bottomNavParams =
            (ViewGroup.MarginLayoutParams) bottomNav.getLayoutParams();
        bottomNavParams.bottomMargin = bottom;
        bottomNav.setLayoutParams(bottomNavParams);
    }
}
```

### 3. CoordinatorLayout with Edge-to-Edge

```java
public class CoordinatorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);

        ImmersionBar.with(this)
            .statusBarColor(Color.TRANSPARENT)
            .statusBarDarkFont(true)
            .setOnInsetsChangeListener((top, bottom, left, right) -> {
                // Add top padding to AppBarLayout
                appBarLayout.setPadding(
                    appBarLayout.getPaddingLeft(),
                    top,
                    appBarLayout.getPaddingRight(),
                    appBarLayout.getPaddingBottom()
                );
            })
            .init();
    }
}
```

## Layout XML Considerations

When using Edge-to-Edge mode, ensure your root layout doesn't use `fitsSystemWindows="true"`:

```xml
<!-- ❌ Don't use fitsSystemWindows with Edge-to-Edge -->
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">
    <!-- content -->
</androidx.constraintlayout.widget.ConstraintLayout>

<!-- ✅ Instead, handle insets programmatically -->
<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <!-- content -->
</androidx.constraintlayout.widget.ConstraintLayout>
```

## Troubleshooting

### Issue: Content overlaps with status bar

**Solution**: Make sure you're applying the top inset from the listener:

```java
.setOnInsetsChangeListener((top, bottom, left, right) -> {
    findViewById(R.id.content).setPadding(0, top, 0, 0);
})
```

### Issue: Bottom navigation overlaps with navigation bar

**Solution**: Apply bottom inset:

```java
.setOnInsetsChangeListener((top, bottom, left, right) -> {
    findViewById(R.id.bottom_nav).setPadding(0, 0, 0, bottom);
})
```

### Issue: Edge-to-Edge not working on Android 15

**Check**:
1. Is `edgeToEdgeEnabled` set to `true`? (default is true)
2. Is your app's `targetSdkVersion` >= 35?
3. Enable debug logging to verify which mode is active

```java
ImmersionBar.with(this)
    .debugPrintVersionInfo(true)
    .edgeToEdgeEnabled(true)
    .init();
```

### Issue: Insets listener not being called

**Possible causes**:
1. Running on Android < 15 (listener only works on Android 15+)
2. Edge-to-Edge disabled via `edgeToEdgeEnabled(false)`

**Solution**: Check version first:
```kotlin
if (isAndroid15OrAbove) {
    immersionBar {
        setOnInsetsChangeListener { top, bottom, left, right ->
            // Handle insets
        }
    }
} else {
    // Use traditional approach
    immersionBar {
        fitsSystemWindows(true)
    }
}
```

## Best Practices

1. **Always handle insets on Android 15+**: Don't rely on `fitsSystemWindows`, use the insets listener
2. **Keep status/navigation bars transparent**: `Color.TRANSPARENT` for full Edge-to-Edge effect
3. **Apply insets to correct views**: Toolbar gets top, bottom nav gets bottom
4. **Test on real Android 15 devices**: Emulator behavior may differ
5. **Use debug logging during development**: `debugPrintVersionInfo(true)` helps identify issues
6. **Maintain backward compatibility**: Your code should work on all Android versions 4.4+

---

For more information, see:
- [ANDROID_15_ADAPTATION.md](ANDROID_15_ADAPTATION.md) - Detailed adaptation documentation
- [Official Android Edge-to-Edge Guide](https://developer.android.com/develop/ui/views/layout/edge-to-edge)
