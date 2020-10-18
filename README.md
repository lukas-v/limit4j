# limit4j
Simple to use rate limiting library for Java applications.

## Example

### Rate limiting entire web application

```java
@WebFilter(urlPatterns={"/*"})
public class AwsomeWebFilter extends UsageLimitsFilter {

  /**
   * Limits usage to 100 requests in the last minute.
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

### Session and path based rate limiting
```java
@WebFilter(urlPatterns={"/*"})
public class AwsomeWebFilter extends UsageLimitsFilter {

  /**
   * Builder for creating rate limiting instances for each session.
   */
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
      // returning null results to HttpServletResponse.SC_UNAUTHORIZED
      return null;
    }
    else
    {
      String name = AwsomeWebFilter.class.getName();
      
      @SuppressWarnings("unchecked")
      FineGrainedLimits<String> limits = (FineGrainedLimits<String>)session
        .getAttribute(name);
      
      if(limits == null)
      {
        Map<String, UsageLimits> tmp = new HashMap<>();
        for(String path : Arrays.asList("/", "/path_1", "/path_2")) {
          tmp.put(path, defaultLimit.create());
        }
        
        limits = FineGrainedLimitsBuilder.fromMap(tmp);
        
        session.setAttribute(name, limits);
      }
      
      String group = httpRequest.getPathInfo();
      
      UsageLimits limit = limits.forGroup(group);
      if(limit == null)
      {
        // unknown paths will be rejected by HttpServletResponse.SC_FORBIDDEN
        limit = RejectedUsage.getInstance();
      }
      
      return limit;
    }
  }

}
```
