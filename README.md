# limit4j

## Example

```java
@WebFilter(urlPatterns={"/*"})
public class AwsomeWebFilter extends UsageLimitsFilter {

  /**
   * Limits usage to 100 request during the latest minute.
   */
  private final UsageLimits limits = UsageLimitsBuilder
    .minute()
    .withFramesSplitBySeconds(1)
    .withTotalLimit(100)
    .create();

  @Override
  protected UsageLimits limits(ServletRequest request) {
    return limits;
  }

}
```
