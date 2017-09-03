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

```java
@WebFilter(urlPatterns={"/*"})
public class AwsomeWebFilter extends UsageLimitsFilter {

  private final UsageLimitsBuilder defaultLimit = UsageLimitsBuilder
    .minute()
    .withFramesSplitBySeconds(1)
    .withTotalLimit(5);

  @Override
  protected UsageLimits limits(ServletRequest request) {
    HttpServletRequest httpRequest = (HttpServletRequest)request;
    
    HttpSession session = httpRequest.getSession(false);
    if(session == null)
    {
      // HttpServletResponse.SC_UNAUTHORIZED
      return null;
    }
    else
    {
      String name = UsageLimitsFilterForTest.class.getName();
      
      @SuppressWarnings("unchecked")
      FineGrainedLimits<String> limits = (FineGrainedLimits<String>)session
        .getAttribute(name);
      
      if(limits == null)
      {
        Map<String, UsageLimits> tmp = new HashMap<>();
        for(String path : Arrays.asList("/", "/path_1", "/path_2")) {
          tmp.put(path, defaultLimit.create());
        }
        
        limits = DefaultFineGrainedLimits.from(tmp);
        
        session.setAttribute(name, limits);
      }
      
      String group = httpRequest.getPathInfo();
      
      UsageLimits limit = limits.forGroup(group);
      if(limit == null)
      {
        // HttpServletResponse.SC_FORBIDDEN
        limit = RejectedUsage.getInstance();
      }
      
      return limit;
    }
  }

}
```
