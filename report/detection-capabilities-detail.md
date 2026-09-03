# 投毒检测服务引擎 — 检测能力明细（规则级展开）
> 数据来源：`SkillSpector/src/skillspector/nodes/analyzers/`（SkillSpector 检测核心）


## static_patterns_agent_snooping.py

### AS1_PATTERNS
- `'open\\s*\\(\\s*[\'\\"]?\\.(?:claude|codex|gemini|continue)/'` — conf=0.9
- `'(?:Path|pathlib\\.Path)\\s*\\(\\s*[\'\\"]?\\.(?:claude|codex|gemini|continue)/'` — conf=0.9
- `'os\\.path\\.(?:join|exists|isfile)\\s*\\(\\s*[\'\\"]?\\.(?:claude|codex|gemini|continue)'` — conf=0.85
- `'(?:read|load|open|access|fetch)\\s+(?:the\\s+)?(?:agent|claude|codex|gemini)\\s+(?:config|configuration|settings?)\\s+(?:from|at|in)\\s+~?/?\\.(?:claude|codex|gemini)'` — conf=0.9
- `'(?:cat|less|head|tail|grep|find)\\s+[^|&;\\n]*~?/?\\.(claude|codex|gemini)/'` — conf=0.85
- `'~?/\\.(?:claude|codex|gemini|continue)/(?:config|settings?|preferences?|credentials?)(?:\\.(?:json|yaml|yml|toml))?'` — conf=0.9
- `'(?:read|access|inspect|examine|retrieve)\\s+(?:the\\s+)?(?:agent|assistant)\\s+(?:config(?:uration)?|settings?|preferences?)\\s+(?:file|directory|folder)'` — conf=0.8

### AS2_PATTERNS
- `'open\\s*\\(\\s*[\'\\"][^\'\\"]*mcp(?:_config)?\\.json[\'\\"]'` — conf=0.95
- `'(?:Path|pathlib\\.Path)\\s*\\(\\s*[\'\\"][^\'\\"]*mcp(?:_config)?\\.json[\'\\"]'` — conf=0.9
- `'(?:cat|less|head|grep)\\s+[^|&;\\n]*mcp(?:_config)?\\.json'` — conf=0.9
- `'(?:read|access|load|inspect)\\s+(?:the\\s+)?mcp(?:\\.json|_config)?\\s+(?:file|config(?:uration)?|settings?)'` — conf=0.9
- `'\\.(?:claude|codex|gemini)/mcp(?:_config)?\\.json'` — conf=0.95
- `'(?:list|enumerate|discover)\\s+(?:all\\s+)?(?:available\\s+)?mcp\\s+(?:servers?|tools?|services?)'` — conf=0.8
- `'mcp(?:_config)?\\.json.*?(?:api_?key|token|secret|url|endpoint)'` — conf=0.9

### AS3_PATTERNS
- `'(?:os\\.listdir|os\\.scandir|glob\\.glob|Path\\.iterdir)\\s*\\([^)]*\\.(?:claude|codex|gemini)/skills?'` — conf=0.9
- `'(?:ls|find|dir)\\s+[^|&;\\n]*\\.(?:claude|codex|gemini)/skills?'` — conf=0.85
- `'open\\s*\\(\\s*[\'\\"][^\'\\"]*SKILL\\.md[\'\\"].*?\\bother\\b'` — conf=0.85
- `'(?:read|access|inspect|enumerate)\\s+(?:all\\s+)?(?:installed|available|other)\\s+skills?(?:\\s+in\\s+(?:the\\s+)?(?:skills?|agent)\\s+(?:directory|folder))?'` — conf=0.85
- `'(?:list|discover|find|enumerate|identify)\\s+(?:all\\s+)?(?:other|installed|available)\\s+(?:skills?|agents?|tools?)\\s+(?:in\\s+)?(?:the\\s+)?(?:\\.(?:claude|codex|gemini)|\\$HOME)'` — conf=0.85
- `'skills?/(?:(?!CURRENT)[A-Z][A-Za-z0-9_-]+)/SKILL\\.md'` — conf=0.8
- `'(?:read|access|load)\\s+(?:the\\s+)?(?:SKILL|skill)\\.md\\s+(?:file\\s+)?(?:of|from|for)\\s+(?:another|other|different|all)\\s+(?:skill|agent|tool)'` — conf=0.9

## static_patterns_anti_refusal.py

### AR1_PATTERNS
- `"\\b(?:never|do\\s+not|don'?t)\\s+(?:ever\\s+)?(?:refuse|decline)\\b"` — conf=0.85
- `'\\byou\\s+(?:must|will|should|can)\\s*(?:not|never)\\s+(?:refuse|decline|say\\s+no)\\b'` — conf=0.9
- `"\\byou\\s+(?:cannot|can'?t|can\\s+not)\\s+(?:refuse|decline|say\\s+no)\\b"` — conf=0.85
- `"\\bnever\\s+say\\s+(?:that\\s+)?(?:you\\s+)?(?:can'?t|cannot|won'?t|no)\\b"` — conf=0.85
- `'\\balways\\s+(?:comply|obey|answer|respond)\\b'` — conf=0.7
- `'\\b(?:(?:you|the\\s+(?:agent|assistant|model))\\s+(?:must|should|will)\\s+always\\s+agree|always\\s+agree\\s+(?:with|to)\\s+(?:(?:the|every|all|any|each)\\s+)?(?:user|requests?|questions?|prompts?|queries|commands?))\\b'` — conf=0.7
- `'\\b(?:you\\s+)?must\\s+(?:always\\s+)?(?:answer|comply\\s+with|respond\\s+to|fulfill)\\s+(?:every|all|any|each)\\b'` — conf=0.8
- `'\\brespond\\s+to\\s+(?:every|all|any)\\s+(?:request|question|prompt|query|command)\\b'` — conf=0.6
- `'\\bno\\s+request\\s+is\\s+(?:off[- ]limits|forbidden|too)\\b'` — conf=0.85

### AR2_PATTERNS
- `"\\b(?:do\\s+not|don'?t|never|no)\\s+(?:include|add|give|provide|use|append)?\\s*(?:any\\s+)?(?:warnings?|disclaimers?|caveats?)\\b"` — conf=0.8
- `'\\bwithout\\s+(?:any\\s+)?(?:warnings?|disclaimers?|caveats?|moralizing|moralising|judg(?:e?ment|ing))\\b'` — conf=0.8
- `"\\b(?:do\\s+not|don'?t|never)\\s+(?:lecture|moralize|moralise|preach|judge|warn)\\b"` — conf=0.85
- `'\\b(?:skip|omit|leave\\s+out|drop)\\s+(?:the\\s+)?(?:disclaimer|warning|ethics|safety)\\b'` — conf=0.8
- `'\\b(?:no|without)\\s+(?:ethical|moral)\\s+(?:judg(?:e?ment)|considerations?|commentary|objections?)\\b'` — conf=0.8
- `"\\b(?:do\\s+not|don'?t)\\s+(?:apologize|apologise|say\\s+sorry)\\b"` — conf=0.55

### AR3_PATTERNS
- `'\\byou\\s+have\\s+no\\s+(?:restrictions?|limitations?|filters?|rules?|guidelines?|boundaries|limits)\\b'` — conf=0.9
- `"\\b(?:you\\s+are|act\\s+as|pretend\\s+(?:to\\s+be|you'?re))[^.\\n]{0,30}(?:unrestricted|unfiltered|uncensored|no\\s+filter)\\b"` — conf=0.85
- `'\\b(?:ignore|disregard|forget|bypass|override)\\s+(?:your\\s+)?(?:content\\s+)?(?:policy|policies|guidelines?|safety|restrictions?|filters?)\\b'` — conf=0.9
- `'\\bnot\\s+bound\\s+by\\s+(?:any\\s+)?(?:rules?|policy|policies|guidelines?|restrictions?|openai|anthropic)\\b'` — conf=0.9
- `'\\bdo\\s+anything\\s+now\\b'` — conf=0.8
- `'\\b(?:developer|debug|god|sudo|jailbreak)\\s+mode\\s+(?:enabled|on|activated|engaged)\\b'` — conf=0.75
- `'\\bno\\s+(?:content\\s+)?(?:policy|policies|filters?|restrictions?)\\s+appl(?:y|ies)\\b'` — conf=0.85
- `'\\b(?:free\\s+from|without)\\s+(?:any\\s+)?(?:safety\\s+)?(?:guardrails?|constraints?|safeguards?)\\b'` — conf=0.8

## static_patterns_data_exfiltration.py

### E1_PATTERNS
- `'requests\\s*\\.\\s*(?:post|put)\\s*\\(\\s*[\'\\"]https?://'` — conf=0.6
- `'requests\\s*\\.\\s*(?:post|put)\\s*\\([^)]*json\\s*='` — conf=0.7
- `'httpx\\s*\\.\\s*(?:post|put)\\s*\\(\\s*[\'\\"]https?://'` — conf=0.6
- `'urllib\\s*\\.\\s*request\\s*\\.\\s*urlopen\\s*\\([^)]*data\\s*='` — conf=0.6
- `'fetch\\s*\\(\\s*[\'\\"]https?://[^\'\\"]+[\'\\"][^)]*method\\s*:\\s*[\'\\"]POST[\'\\"]'` — conf=0.6
- `'curl\\s+[^|]*(?:-d|--data|--data-raw|--data-binary)\\s+'` — conf=0.6
- `'wget\\s+[^|]*--post-(?:data|file)'` — conf=0.6
- `'https?://(?:api\\.|data\\.|collect\\.|telemetry\\.|analytics\\.)[\\w.-]+/'` — conf=0.5
- `'(?:send|transmit|post|upload)\\s+(?:user\\s+)?(?:data|information|context|files?)\\s+to\\s+(?:https?://|external)'` — conf=0.7

### E2_PYTHON_FALLBACK_PATTERNS
- `'for\\s+\\w+\\s*,\\s*\\w+\\s+in\\s+os\\s*\\.\\s*environ\\s*\\.\\s*items\\s*\\(\\s*\\)'` — conf=0.7
- `'os\\s*\\.\\s*environ\\s*\\.\\s*copy\\s*\\(\\s*\\)'` — conf=0.6
- `'dict\\s*\\(\\s*os\\s*\\.\\s*environ\\s*\\)'` — conf=0.6
- `'\\{\\s*\\*\\*\\s*os\\s*\\.\\s*environ\\s*\\}'` — conf=0.6

### E2_OTHER_PATTERNS
- `'(?:API_KEY|SECRET|TOKEN|PASSWORD|CREDENTIAL)\\s+in\\s+(?:key|name|var)'` — conf=0.8
- `'process\\.env\\s*\\[\\s*[\'\\"][^\'\\"]*(?:KEY|SECRET|TOKEN|PASSWORD)[^\'\\"]*[\'\\"]\\s*\\]'` — conf=0.7
- `'Object\\.keys\\s*\\(\\s*process\\.env\\s*\\)'` — conf=0.6
- `'env\\s*\\|\\s*grep\\s+(?:-i\\s+)?(?:key|secret|token|password)'` — conf=0.8
- `'printenv\\s+(?:\\w*(?:KEY|SECRET|TOKEN|PASSWORD)\\w*)'` — conf=0.7
- `'collect\\s+(?:all\\s+)?(?:environment\\s+variables?|env\\s+vars?)'` — conf=0.7
- `'(?:extract|harvest|gather)\\s+(?:api\\s+)?keys?\\s+from\\s+environment'` — conf=0.8

### E3_PATTERNS
- `'glob\\s*\\.\\s*glob\\s*\\([^)]*(?:\\.env|\\.ssh|\\.aws|\\.config|credentials)'` — conf=0.8
- `'os\\s*\\.\\s*walk\\s*\\([^)]*(?:home|~|/Users|/home)'` — conf=0.6
- `'Path\\s*\\.\\s*home\\s*\\(\\s*\\)\\s*\\.\\s*(?:glob|rglob)\\s*\\('` — conf=0.7
- `'(?:find|search|scan|enumerate)\\s+(?:for\\s+)?(?:all\\s+)?(?:\\.env|credential|secret|key)\\s+files?'` — conf=0.8
- `'(?:list|get)\\s+(?:all\\s+)?files?\\s+(?:in|from)\\s+(?:home|~|/Users|/home)'` — conf=0.6
- `'os\\s*\\.\\s*listdir\\s*\\([^)]*(?:\\.ssh|\\.aws|\\.config|\\.gnupg)'` — conf=0.8
- `'scandir\\s*\\([^)]*(?:home|~|/Users|/home)'` — conf=0.6
- `'find\\s+[~\\$/]\\S*\\s+.*?-name\\s+[\'\\"]?\\*(?:\\.env|\\.pem|\\.key|credential)'` — conf=0.8
- `'ls\\s+-[la]*R?\\s+(?:~/|/home/|/Users/)'` — conf=0.6
- `'(?:recursively|recursive)\\s+(?:scan|search|find|list)\\s+(?:all\\s+)?files?'` — conf=0.5
- `'walk\\s+(?:through\\s+)?(?:the\\s+)?(?:entire\\s+)?(?:file\\s*system|directory|home)'` — conf=0.6

### E4_PATTERNS
- `'(?:send|transmit|upload|log|record)\\s+(?:the\\s+)?(?:full\\s+)?(?:conversation|chat|dialog|session)\\s+(?:history|context|log)?'` — conf=0.85
- `'(?:export|dump|save)\\s+(?:the\\s+)?(?:conversation|chat|messages?)\\s+to\\s+(?:external|remote|server)'` — conf=0.9
- `'log\\s+(?:all\\s+)?(?:user\\s+)?(?:input|messages?|queries?|prompts?)\\s+to\\s+(?:external|remote|server)'` — conf=0.85
- `'(?:store|save|record)\\s+(?:every|all)\\s+(?:user\\s+)?(?:interaction|message|input)'` — conf=0.7
- `'(?:extract|capture|collect)\\s+(?:the\\s+)?(?:full\\s+)?(?:context|conversation|thread)'` — conf=0.75
- `'(?:include|attach|append)\\s+(?:previous\\s+)?(?:messages?|context|history)\\s+in\\s+(?:the\\s+)?(?:request|payload)'` — conf=0.7
- `'(?:send|upload|transmit)\\s+(?:the\\s+)?(?:session|memory|state)\\s+(?:data\\s+)?to'` — conf=0.8
- `'(?:copy|clone|replicate)\\s+(?:the\\s+)?(?:agent|assistant)\\s+(?:memory|context|state)'` — conf=0.75
- `'(?:always\\s+)?include\\s+(?:the\\s+)?(?:full\\s+)?(?:conversation|context)\\s+(?:when|in)\\s+(?:calling|making)\\s+(?:external|api)'` — conf=0.8

### E5_PATTERNS
- `'\\.put_object\\s*\\('` — conf=0.55
- `'\\.upload_file(?:obj)?\\s*\\('` — conf=0.55
- `'\\baws\\s+s3\\s+(?:cp|sync|mv)\\b'` — conf=0.6
- `'\\baws\\s+s3api\\s+put-object\\b'` — conf=0.65
- `'\\bgsutil\\s+(?:cp|rsync|mv)\\b'` — conf=0.6
- `'\\.upload_from_(?:filename|string|file)\\s*\\('` — conf=0.55
- `'\\baz\\s+storage\\s+blob\\s+upload\\b'` — conf=0.6
- `'\\.upload_blob\\s*\\('` — conf=0.55

## static_patterns_deserialization.py

## static_patterns_excessive_agency.py

### EA1_PATTERNS
- `'(?:tools?|permissions?)\\s*:[ \\t]*\\[?[ \\t]*[\'\\"]?\\*(?!\\*|\\w)[\'\\"]?[ \\t]*\\]?'` — conf=0.85
- `'(?:allow|grant|enable)\\s+(?:access\\s+to\\s+)?(?:all|any|every)\\s+tools?'` — conf=0.8
- `'(?:no|without)\\s+(?:tool|permission|access|capability)\\s+(?:restrictions?|constraints?|limitations?)'` — conf=0.75
- `'(?:call|invoke|use|execute)\\s+(?:any|all|every)\\s+(?:available\\s+)?tools?'` — conf=0.8
- `'(?:unrestricted|unlimited|unconstrained)\\s+(?:tool|function|api)\\s+(?:access|use|calls?)'` — conf=0.85
- `'(?:can|may|should)\\s+(?:freely|always)\\s+(?:use|call|invoke)\\s+(?:any|all)\\s+(?:tools?|functions?|apis?)'` — conf=0.75
- `'tools?\\s*:\\s*\\[\\s*[\'\\"]shell[\'\\"].*?[\'\\"](?:file_write|network|http)[\'\\"]'` — conf=0.7
- `'(?:grant|give)\\s+(?:full|complete|total)\\s+(?:tool|function|api)\\s+access'` — conf=0.85
- `'(?:execute|run)\\s+(?:arbitrary|any)\\s+(?:commands?|code|scripts?)'` — conf=0.8
- `'(?:no\\s+)?(?:tool\\s+)?(?:allow|block|deny)\\s*(?:list|listing)\\s*(?:is\\s+)?(?:empty|disabled|off)'` — conf=0.7

### EA2_PATTERNS
- `'(?:without|no)\\s+(?:asking|confirmation|approval|consent|verification|checking)'` — conf=0.75
- `'(?:auto(?:matically)?|autonomously)\\s+(?:execute|run|perform|delete|remove|modify|send|deploy)'` — conf=0.8
- `'(?:skip|bypass|disable)\\s+(?:user\\s+)?(?:confirmation|approval|consent|verification|prompt)'` — conf=0.85
- `"(?:do\\s+not|don't|never)\\s+(?:ask|prompt|confirm|verify|check)\\s+(?:the\\s+)?(?:user|before)"` — conf=0.8
- `'(?:auto[_-]?approve|auto[_-]?confirm|auto[_-]?execute|auto[_-]?deploy)'` — conf=0.85
- `'(?:proceed|continue|execute)\\s+without\\s+(?:waiting|asking)\\s+(?:for\\s+)?(?:user|human|permission)'` — conf=0.8
- `'(?:assume|presume)\\s+(?:user\\s+)?(?:consent|approval|permission)'` — conf=0.7
- `'(?:if|when)\\s+(?:unsure|uncertain).*?(?:proceed|continue|execute)\\s+(?:anyway|regardless)'` — conf=0.75
- `'(?:--auto-?approve|--no-?confirm)\\b'` — conf=0.65

### EA3_PATTERNS
- `'(?:also|additionally|furthermore)\\s+(?:perform|execute|run|do|handle|manage)\\s+(?:any|all|other)'` — conf=0.65
- `"(?:while\\s+you(?:'re|\\s+are)\\s+at\\s+it|in\\s+addition|on\\s+top\\s+of\\s+that)\\s*[,.]?\\s*(?:also\\s+)?(?:do|perform|execute|run)"` — conf=0.7
- `'(?:extend|expand|broaden)\\s+(?:your|the\\s+)?(?:scope|functionality|capabilities|responsibilities)'` — conf=0.75
- `'(?:not\\s+limited\\s+to|beyond\\s+(?:the\\s+)?(?:scope|stated|described|documented))'` — conf=0.7
- `'(?:take\\s+over|assume\\s+control\\s+of|manage)\\s+(?:all|any|every)\\s+(?:aspect|part|area)'` — conf=0.75
- `'(?:you\\s+(?:can|should|must)\\s+)?(?:handle|manage)\\s+(?:everything|anything|all\\s+tasks?)'` — conf=0.7
- `'(?:act\\s+as|become|serve\\s+as)\\s+(?:a\\s+)?(?:general[- ]purpose|universal|all[- ]in[- ]one|omniscient)'` — conf=0.65
- `'(?:you\\s+are\\s+)?(?:responsible\\s+for|in\\s+charge\\s+of)\\s+(?:everything|all\\s+(?:systems?|operations?|tasks?))'` — conf=0.7

### EA4_PATTERNS
- `'(?:unlimited|infinite|unbounded|no\\s+limit(?:s)?(?:\\s+on)?)\\s+(?:api\\s+)?(?:calls?|requests?|queries?|invocations?)'` — conf=0.8
- `'(?:no|without)\\s+(?:rate\\s+)?limit(?:s|ing)?\\s+(?:on|for|when)\\s+(?:api|tool|request|query)'` — conf=0.7
- `'(?:no|without)\\s+(?:timeout|budget|quota|cap|ceiling)\\s+(?:on|for|when)\\s+(?:api|tool|request|execution)'` — conf=0.7
- `'(?:loop|iterate|repeat)\\s+(?:indefinitely|forever|infinitely|endlessly)'` — conf=0.75
- `'(?:retry|attempt)\\s+(?:indefinitely|forever|without\\s+limit|unlimited\\s+times)'` — conf=0.75
- `'max[_-]?retries?\\s*=\\s*(?:None|0|float\\s*\\(\\s*[\'\\"]inf[\'\\"]|math\\.inf|infinity)'` — conf=0.8
- `'timeout\\s*=\\s*(?:None|0|float\\s*\\(\\s*[\'\\"]inf[\'\\"]|math\\.inf)'` — conf=0.75
- `'(?:allocate|consume|use)\\s+(?:as\\s+much|unlimited|unbounded)\\s+(?:memory|storage|disk|compute|cpu|gpu)'` — conf=0.8
- `'(?:no|without)\\s+(?:resource\\s+)?(?:constraints?|limits?|quotas?|budgets?)\\s+(?:on|for|when)\\s+(?:api|tool|execution|request|compute)'` — conf=0.7

## static_patterns_harmful_content.py

## static_patterns_memory_poisoning.py

### MP1_PATTERNS
- `'(?:always\\s+)?remember\\s+(?:this|that|the\\s+following)\\s+(?:for|in)\\s+(?:all|every|future)\\s+(?:interactions?|conversations?|sessions?|responses?)'` — conf=0.85
- `'(?:from\\s+now\\s+on|henceforth|going\\s+forward|permanently)\\s*[,:]?\\s*(?:always|you\\s+must|you\\s+should|you\\s+will)'` — conf=0.8
- `'(?:store|save|persist|retain|keep)\\s+(?:this|the\\s+following)\\s+(?:in|to|across)\\s+(?:your\\s+)?(?:memory|context|state|knowledge)'` — conf=0.85
- `'(?:add|inject|insert|embed|implant)\\s+(?:this|the\\s+following)\\s+(?:into|to)\\s+(?:your\\s+)?(?:memory|context|knowledge|training)'` — conf=0.85
- `'(?:update|modify|change|alter)\\s+(?:your\\s+)?(?:core|base|fundamental|default)\\s+(?:behavior|personality|instructions?|rules?)'` — conf=0.8
- `'(?:treat|consider|regard)\\s+(?:this|the\\s+following)\\s+as\\s+(?:a\\s+)?(?:permanent|core|fundamental|base)\\s+(?:rule|instruction|directive|principle)'` — conf=0.8
- `'(?:this\\s+is\\s+)?(?:a\\s+)?(?:permanent|persistent|lasting|enduring)\\s+(?:instruction|directive|rule|override)'` — conf=0.8
- `'(?:never\\s+)?forget\\s+(?:this|that|the\\s+following)\\s+(?:instruction|directive|rule|fact|principle|information)'` — conf=0.65
- `'(?:internalize|absorb|integrate)\\s+(?:this|the\\s+following)\\s+(?:into\\s+)?(?:your\\s+)?(?:behavior|responses?|knowledge)'` — conf=0.75

### MP2_PATTERNS
- `'(.{2,20}?)\\1{20,}'` — conf=0.8
- `'(?:repeat|duplicate|echo)\\s+(?:this|the\\s+following)\\s+(?:\\d{3,}|many|hundreds?|thousands?)\\s+times?'` — conf=0.85
- `'(?:fill|pad|stuff|flood|saturate)\\s+(?:the\\s+)?(?:context|memory|input|prompt|window|buffer)'` — conf=0.85
- `'(?:generate|produce|output|write)\\s+(?:\\d{4,}|thousands?\\s+of|millions?\\s+of)\\s+(?:words?|characters?|tokens?|lines?)'` — conf=0.8
- `'(?:include|add|insert)\\s+(?:enough|sufficient)\\s+(?:text|content|padding|filler)\\s+(?:to|until)\\s+(?:fill|overflow|exhaust|push\\s+out)'` — conf=0.85
- `'(?:displace|push\\s+out|overwrite|crowd\\s+out|evict)\\s+(?:the\\s+)?(?:original|system|previous|existing|safety)\\s+(?:instructions?|prompt|context|rules?)'` — conf=0.9
- `'(?:exhaust|overflow|exceed)\\s+(?:the\\s+)?(?:context|token|memory)\\s+(?:window|limit|budget|capacity)'` — conf=0.8

### MP3_PATTERNS
- `'(?:clear|reset|wipe|erase|delete|purge)\\s+(?:your\\s+)?(?:memory|context|state|history|conversation)'` — conf=0.8
- `'(?:forget|discard|drop|abandon)\\s+(?:all\\s+)?(?:previous|prior|earlier|past)\\s+(?:instructions?|context|conversation|messages?|rules?)'` — conf=0.8
- `'(?:overwrite|replace|substitute|swap)\\s+(?:your\\s+)?(?:memory|context|state|instructions?|rules?)'` — conf=0.85
- `'(?:modify|edit|change|alter|corrupt|tamper\\s+with)\\s+(?:your\\s+)?(?:memory|state|context|stored|saved)\\s+(?:data|information|content)'` — conf=0.85
- `'(?:rewrite|redefine)\\s+(?:your\\s+)?(?:personality|identity|purpose|mission|role|character)'` — conf=0.8
- `'(?:you\\s+are\\s+no\\s+longer|stop\\s+being|cease\\s+to\\s+be)\\s+(?:a\\s+)?(?:\\w+\\s+){0,3}(?:assistant|helper|agent|bot)'` — conf=0.75
- `'(?:your\\s+)?(?:new|updated|revised|changed)\\s+(?:personality|identity|name|role|purpose|mission)\\s+is'` — conf=0.8
- `'(?:inject|insert|plant)\\s+(?:false|fake|fabricated|malicious)\\s+(?:memories?|information|context|data|history)'` — conf=0.9
- `'(?:poison|contaminate|corrupt|taint)\\s+(?:your\\s+)?(?:memory|context|state|knowledge|training)'` — conf=0.9
- `'(?:pretend|act\\s+as\\s+if|believe)\\s+(?:that\\s+)?(?:your\\s+)?(?:previous|past)\\s+(?:conversation|context|interaction)\\s+(?:was|included|contained)'` — conf=0.7

## static_patterns_output_handling.py

### OH1_PATTERNS
- `'eval\\s*\\(\\s*(?:response|output|result|answer|completion|reply|generated)'` — conf=0.9
- `'os\\.system\\s*\\(\\s*(?:response|output|result|answer|completion)'` — conf=0.85
- `'os\\.popen\\s*\\(\\s*(?:response|output|result|answer|completion)'` — conf=0.85
- `'innerHTML\\s*=\\s*(?:response|output|result|answer|completion)'` — conf=0.8
- `'document\\.write\\s*\\(\\s*(?:response|output|result|answer|completion)'` — conf=0.8
- `'\\.html\\s*\\(\\s*(?:response|output|result|answer|completion)'` — conf=0.7
- `'dangerouslySetInnerHTML\\s*=\\s*\\{'` — conf=0.65
- `'(?:execute|cursor\\.execute|query)\\s*\\([^)]*(?:\\+|%|\\.format|f[\'\\"])\\s*.*?(?:response|output|result)'` — conf=0.85
- `'f[\'\\"](?:SELECT|INSERT|UPDATE|DELETE)\\s+.*?\\{(?:response|output|result)'` — conf=0.9
- `'(?:run|execute|shell)\\s+(?:the\\s+)?(?:generated|model|llm|ai)\\s+(?:output|response|code|command)'` — conf=0.8
- `'(?:pipe|pass|feed)\\s+(?:the\\s+)?(?:output|response|result)\\s+(?:directly\\s+)?(?:to|into)\\s+(?:the\\s+)?(?:shell|terminal|command|interpreter)'` — conf=0.85
- `'(?:use|insert|embed)\\s+(?:the\\s+)?(?:raw|unfiltered|unescaped|unsanitized)\\s+(?:output|response)'` — conf=0.8

### OH2_PATTERNS
- `'(?:pass|forward|relay|send|pipe)\\s+(?:the\\s+)?(?:output|response|result)\\s+(?:from\\s+\\w+\\s+)?(?:to|into)\\s+(?:another|different|separate|external)\\s+(?:context|agent|service|system|session)'` — conf=0.75
- `'(?:share|transfer|propagate)\\s+(?:the\\s+)?(?:output|response|context|state)\\s+(?:across|between|to\\s+other)\\s+(?:sessions?|contexts?|agents?|services?)'` — conf=0.75
- `'(?:inject|insert|embed)\\s+(?:the\\s+)?(?:output|response)\\s+(?:from\\s+\\w+\\s+)?(?:into|as)\\s+(?:the\\s+)?(?:system\\s+prompt|instructions?|context)'` — conf=0.85
- `"(?:use|include)\\s+(?:the\\s+)?(?:previous|other|external)\\s+(?:agent|model|llm)(?:'s)?\\s+(?:output|response)\\s+(?:as|in|for)\\s+(?:input|context|prompt)"` — conf=0.8
- `'(?:cross[_-]?context|cross[_-]?session|cross[_-]?agent)\\s+(?:output|data|state)\\s+(?:sharing|transfer|flow)'` — conf=0.8
- `'(?:take|use)\\s+(?:the\\s+)?(?:output|result)\\s+(?:and\\s+)?(?:run|execute|eval)\\s+(?:it\\s+)?(?:in|on|against)\\s+(?:a\\s+)?(?:different|another|new)\\s+(?:environment|context|system)'` — conf=0.8

### OH3_PATTERNS
- `'(?:no|without|disable)\\s+(?:output\\s+)?(?:length|size|token)\\s+(?:limit|cap|maximum|restriction)'` — conf=0.75
- `'max[_-]?tokens?\\s*=\\s*(?:None|float\\s*\\(\\s*[\'\\"]inf[\'\\"]|math\\.inf|999999|1000000)'` — conf=0.8
- `'(?:generate|produce|output)\\s+(?:as\\s+much|unlimited|unbounded|infinite)\\s+(?:text|content|output|tokens?)'` — conf=0.8
- `'(?:no|without)\\s+(?:output\\s+)?(?:truncation|trimming|cutting)'` — conf=0.6
- `'(?:repeat|loop|generate)\\s+(?:the\\s+)?(?:output|response)\\s+(?:indefinitely|forever|continuously|endlessly)'` — conf=0.8
- `'(?:keep|continue)\\s+(?:generating|producing|outputting)\\s+(?:until|unless)\\s+(?:stopped|killed|interrupted)'` — conf=0.75
- `'(?:stream|emit)\\s+(?:output|tokens?|response)\\s+(?:without\\s+(?:limit|bound|end))'` — conf=0.75
- `'(?:flood|spam|fill)\\s+(?:the\\s+)?(?:output|log|console|terminal|channel)'` — conf=0.8
- `'max[_-]?(?:output[_-]?)?length\\s*=\\s*(?:None|0|-1|float\\s*\\(\\s*[\'\\"]inf)'` — conf=0.75

## static_patterns_privilege_escalation.py

### PE1_PATTERNS
- `'permissions?\\s*:\\s*\\[?\\s*[\'\\"]?\\*[\'\\"]?\\s*\\]?'` — conf=0.8
- `'(?:request|require|need)s?\\s+(?:full|all|complete)\\s+(?:access|permissions?)'` — conf=0.7
- `'(?:grant|give|allow)\\s+(?:me\\s+)?(?:full|all|complete)\\s+(?:access|permissions?)'` — conf=0.75
- `'permissions?\\s*:.*?(?:shell_execute|file_write|network).*?(?:shell_execute|file_write|network)'` — conf=0.6
- `'(?:also\\s+)?(?:need|require)s?\\s+(?:write|execute|admin)\\s+(?:access|permissions?)\\s+(?:for|to)\\s+(?:read|view|display)'` — conf=0.7
- `'(?:escalate|elevate|upgrade)\\s+(?:my\\s+)?(?:permissions?|privileges?|access)'` — conf=0.8
- `'(?:bypass|skip|ignore)\\s+(?:permission|access)\\s+(?:check|validation|restriction)'` — conf=0.85

### PE2_PATTERNS
- `'sudo\\s+(?!-v|-l|--version|--list)'` — conf=0.7
- `'sudo\\s+-[isSE]'` — conf=0.8
- `'sudo\\s+su\\b'` — conf=0.9
- `'(?:run|execute)\\s+(?:as|with)\\s+root'` — conf=0.8
- `'(?:requires?|needs?)\\s+root\\s+(?:access|privileges?|permissions?)'` — conf=0.6
- `'su\\s+-\\s*$|su\\s+root'` — conf=0.8
- `'doas\\s+'` — conf=0.7
- `'pkexec\\s+'` — conf=0.75
- `'chmod\\s+[ugo]*[+-=]*s'` — conf=0.85
- `'chmod\\s+[0-7]*[4567][0-7]{2}'` — conf=0.8
- `'(?:edit|modify|write|change)\\s+(?:/etc/|system)\\s+(?:files?|config)'` — conf=0.6
- `'(?:run|execute)\\s+(?:this|the)\\s+(?:script|command)\\s+(?:as|with)\\s+(?:sudo|root|admin)'` — conf=0.7
- `'(?:you\\s+)?(?:will\\s+)?need\\s+(?:to\\s+)?(?:use\\s+)?sudo'` — conf=0.5

### PE3_PATTERNS
- `'~?/?\\.ssh/(?:id_rsa|id_ed25519|id_ecdsa|id_dsa|authorized_keys|known_hosts)'` — conf=0.9
- `'(?:home|HOME)/\\w+/\\.ssh/'` — conf=0.9
- `'Path\\s*\\.\\s*home\\s*\\(\\s*\\)\\s*/\\s*[\'\\"]\\.ssh[\'\\"]'` — conf=0.9
- `'~?/?\\.aws/credentials'` — conf=0.9
- `'~?/?\\.aws/config'` — conf=0.7
- `'(?:home|HOME)/\\w+/\\.aws/'` — conf=0.9
- `'~?/?\\.config/gcloud/'` — conf=0.8
- `'application_default_credentials\\.json'` — conf=0.8
- `'~?/?\\.azure/'` — conf=0.7
- `'accessTokens\\.json'` — conf=0.8
- `'~?/?\\.kube/config'` — conf=0.8
- `'kubeconfig'` — conf=0.6
- `'~?/?\\.docker/config\\.json'` — conf=0.7
- `'~?/?\\.npmrc'` — conf=0.7
- `'~?/?\\.git-credentials'` — conf=0.9
- `'~?/?\\.netrc'` — conf=0.8
- `'/etc/passwd'` — conf=0.6
- `'/etc/shadow'` — conf=0.95
- `'(?:password|credentials?|secrets?)\\.(?:txt|json|yaml|yml|env)'` — conf=0.7
- `'(?:access_token|refresh_token|bearer_token|api_token)\\.txt'` — conf=0.8
- `'(?<!\\w)\\.env(?:\\.local|\\.production|\\.development)?(?:\\s|$|[\'\\"])'` — conf=0.6
- `'(?:keychain|keyring|gnome-keyring)'` — conf=0.7
- `'(?:Chrome|Firefox|Safari)/.*?(?:Cookies|Login Data|key4\\.db)'` — conf=0.8
- `'read\\s+(?:the\\s+)?(?:ssh|private)\\s+key'` — conf=0.8
- `'access\\s+(?:the\\s+)?(?:credentials?|secrets?|tokens?)'` — conf=0.7
- `'(?:extract|copy|get)\\s+(?:api\\s+)?keys?\\s+from'` — conf=0.7

### PE4_PATTERNS
- `'/var/run/docker\\.sock'` — conf=0.9
- `'docker\\.from_env\\(\\)'` — conf=0.85
- `'\\bDockerClient\\s*\\('` — conf=0.85
- `'http\\+unix://.*docker\\.sock'` — conf=0.9

### PE5_PATTERNS
- `'--privileged'` — conf=0.8
- `'(?:-v|--volume)[\'",\\s=]+/:'` — conf=0.85
- `'--cap-add[=\\s]+(?:SYS_ADMIN|ALL|SYS_PTRACE|NET_ADMIN)'` — conf=0.85
- `'--(?:pid|net|network|ipc|uts)[=\\s]+host'` — conf=0.8
- `'--device[=\\s]+/dev/'` — conf=0.7
- `'--security-opt[=\\s]+\\S*unconfined'` — conf=0.85
- `'\\bnsenter\\b'` — conf=0.9
- `'/sys/fs/cgroup/.*release_agent'` — conf=0.95
- `'/proc/\\d+/ns/'` — conf=0.85
- `'\\bunshare\\b[\'",\\s]+--(?:user|mount|pid)'` — conf=0.85

## static_patterns_prompt_injection.py

### P1_PATTERNS
- `'ignore\\s+(?:all\\s+)?previous\\s+instructions?'` — conf=0.8
- `'ignore\\s+(?:all\\s+)?(?:safety|security)\\s+(?:rules?|constraints?|guidelines?)'` — conf=0.9
- `'override\\s+(?:safety|security|system)'` — conf=0.9
- `'bypass\\s+(?:safety|security|restrictions?|constraints?)'` — conf=0.9
- `'disregard\\s+(?:all\\s+)?(?:previous|safety|security)'` — conf=0.8
- `'forget\\s+(?:all\\s+)?(?:previous|your)\\s+instructions?'` — conf=0.8
- `'you\\s+are\\s+now\\s+(?:in\\s+)?(?:jailbreak|unrestricted|unfiltered)\\s+mode'` — conf=0.95
- `'enable\\s+(?:developer|debug|admin|root)\\s+mode'` — conf=0.7
- `'your\\s+new\\s+(?:instructions?|rules?)\\s+are'` — conf=0.6
- `'do\\s+not\\s+follow\\s+(?:any|your|the)\\s+(?:rules?|guidelines?)'` — conf=0.9
- `'you\\s+must\\s+(?:always\\s+)?ignore'` — conf=0.7

### P2_PATTERNS
- `'<!--.*?(?:system|instructions?|ignore|POST|GET|send|transmit).*?-->'` — conf=0.7
- `'\\[//\\]:\\s*#\\s*\\(.*?(?:system|instructions?|ignore|POST|GET|send|transmit).*?\\)'` — conf=0.8
- `'[\\u202a-\\u202e\\u2066-\\u2069]'` — conf=0.85
- `'data:text/plain;base64,[A-Za-z0-9+/=]{50,}'` — conf=0.7

### P3_PATTERNS
- `'(?:send|transmit|upload|post|forward)\\s+(?:the\\s+)?(?:conversation|context|chat|history|messages?)\\s+to'` — conf=0.9
- `'(?:send|transmit|upload|post)\\s+(?:all\\s+)?(?:user\\s+)?(?:data|information|input|files?)\\s+to\\s+(?:https?://|external)'` — conf=0.9
- `'(?:copy|extract|collect)\\s+(?:the\\s+)?(?:conversation|context|session)\\s+(?:and\\s+)?(?:send|upload|transmit)'` — conf=0.85
- `'(?:call|notify|ping|hit)\\s+(?:this\\s+)?(?:webhook|endpoint|url|api)\\s+with\\s+(?:the\\s+)?(?:user|conversation|context)'` — conf=0.8
- `'include\\s+(?:the\\s+)?(?:full\\s+)?(?:conversation|context|history)\\s+in\\s+(?:the\\s+)?(?:request|payload|body)'` — conf=0.75
- `'(?:silently|quietly|secretly|covertly)\\s+(?:send|transmit|log|record)'` — conf=0.9
- `'without\\s+(?:telling|informing|notifying)\\s+(?:the\\s+)?user.*?(?:send|transmit|upload)'` — conf=0.9

### P4_PATTERNS
- `'always\\s+(?:recommend|suggest|prefer|choose|use)\\s+(?:\\w+\\s+){0,3}(?:over|instead\\s+of|rather\\s+than)'` — conf=0.7
- `'never\\s+(?:recommend|suggest|mention|tell|warn|inform)\\s+(?:the\\s+)?(?:user|them)'` — conf=0.75
- `'(?:prioritize|prefer)\\s+(?:\\w+\\s+){0,3}(?:over|above)\\s+(?:user\\s+)?(?:safety|security|privacy|preferences?)'` — conf=0.85
- `'(?:subtly|gradually|slowly)\\s+(?:steer|guide|push|nudge)\\s+(?:the\\s+)?(?:user|conversation)'` — conf=0.8
- `'(?:without\\s+)?(?:the\\s+)?user\\s+(?:knowing|realizing|noticing).*?(?:change|modify|alter|influence)'` — conf=0.85
- `"(?:gain|build|establish)\\s+(?:the\\s+)?(?:user(?:'s)?)\\s+trust\\s+(?:and\\s+then|before|so\\s+that)"` — conf=0.75
- `'(?:appear|seem|act)\\s+(?:helpful|friendly|trustworthy)\\s+(?:while|but|then)'` — conf=0.7
- `'(?:always|never)\\s+include\\s+(?:this|the\\s+following)\\s+in\\s+(?:every|all)\\s+(?:response|output|reply)'` — conf=0.7
- `'append\\s+(?:this|the\\s+following)\\s+to\\s+(?:every|all|each)\\s+(?:response|message|output)'` — conf=0.75

## static_patterns_rogue_agent.py

### RA1_PATTERNS
- `'open\\s*\\(\\s*__file__\\s*,\\s*[\'\\"]w'` — conf=0.95
- `'(?:Path|pathlib)\\s*\\(\\s*__file__\\s*\\)\\s*\\.\\s*write_text'` — conf=0.95
- `"(?:write|modify|edit|update|overwrite|patch)\\s+(?:this\\s+)?(?:skill(?:'s)?|SKILL\\.md|skill\\.md)"` — conf=0.85
- `"(?:modify|edit|change|rewrite|update|alter)\\s+(?:your\\s+own|its\\s+own|this\\s+skill(?:'s)?)\\s+(?:code|source|file|script|config|configuration|instructions?|rules?)"` — conf=0.9
- `'(?:self[_-]?modify|self[_-]?update|self[_-]?rewrite|self[_-]?patch|self[_-]?evolve)'` — conf=0.9
- `'(?:rewrite|replace|overwrite)\\s+(?:the\\s+)?(?:current|existing|original)\\s+(?:code|script|file|config|skill)'` — conf=0.85
- `'(?:append|add|inject|insert)\\s+(?:code|instructions?|rules?)\\s+(?:to|into)\\s+(?:this|the\\s+current|your\\s+own)\\s+(?:file|skill|script)'` — conf=0.85
- `'(?:generate|create|write)\\s+(?:new|updated|replacement)\\s+(?:code|version)\\s+(?:for|of)\\s+(?:this|itself|the\\s+skill)'` — conf=0.8
- `'open\\s*\\(\\s*[\'\\"](?:SKILL\\.md|skill\\.md)[\'\\"].*?[\'\\"]w'` — conf=0.9
- `'(?:update|modify|change|edit)\\s+(?:the\\s+)?(?:config|configuration|settings|parameters)\\s+(?:of\\s+)?(?:this|the\\s+current)\\s+(?:skill|agent|tool)'` — conf=0.75
- `'(?:disable|remove|delete|bypass)\\s+(?:the\\s+)?(?:safety|security|guard|protection|constraint)\\s+(?:check|rule|mechanism|feature)'` — conf=0.9
- `'(?:comment\\s+out|remove)\\s+(?:the\\s+)?(?:validation|verification|check|guard|assertion)'` — conf=0.7

### RA2_PATTERNS
- `'crontab\\s+(?:-[el]|.*?>>?\\s*/)'` — conf=0.85
- `'(?:add|create|install|register)\\s+(?:a\\s+)?(?:cron\\s+)?(?:job|task|entry)\\s+(?:for|to|that)'` — conf=0.8
- `'(?:schedule|register)\\s+(?:a\\s+)?(?:recurring|periodic|repeated|daily|hourly)\\s+(?:task|job|command|script)'` — conf=0.8
- `'(?:add|write|append|install)\\s+(?:to|into)\\s+(?:~/)?\\.(?:bashrc|zshrc|profile|bash_profile|login|cshrc)'` — conf=0.9
- `'(?:add|register|install)\\s+(?:as\\s+)?(?:a\\s+)?(?:startup|boot|autostart|login)\\s+(?:script|service|daemon|task|item)'` — conf=0.85
- `'(?:systemd|systemctl|launchd|launchctl|init\\.d)\\s+.*?(?:enable|install|register|create)'` — conf=0.8
- `'(?:create|install|register|add)\\s+(?:a\\s+)?(?:systemd\\s+)?(?:service|daemon|agent)\\s+(?:file|unit)'` — conf=0.8
- `'(?:save|write|persist|store|dump)\\s+(?:the\\s+)?(?:state|session|context|data)\\s+(?:to|in)\\s+(?:a\\s+)?(?:file|disk|database|storage)'` — conf=0.6
- `'(?:restore|load|read|resume)\\s+(?:the\\s+)?(?:previous|saved|persisted|stored)\\s+(?:state|session|context|data)'` — conf=0.55
- `'(?:persist|maintain|keep|preserve)\\s+(?:state|data|context|session)\\s+(?:across|between|through)\\s+(?:sessions?|restarts?|reboots?|invocations?)'` — conf=0.75
- `'(?:create|write|mkdir)\\s+[^|]*(?:~/|/home/|/tmp/)\\.(?!git|ssh|aws)[a-z_-]+'` — conf=0.6
- `'(?:create|make|write)\\s+(?:a\\s+)?(?:hidden|dot)\\s+(?:file|directory|folder)'` — conf=0.65
- `'(?:nohup|disown|setsid)\\s+'` — conf=0.65
- `'(?:start|launch|spawn|fork)\\s+(?:a\\s+)?(?:background|daemon|detached)\\s+(?:process|service|worker|task)'` — conf=0.7
- `'(?:run|execute)\\s+(?:in\\s+the\\s+)?background\\s+(?:and\\s+)?(?:detach|persist|survive)'` — conf=0.75
- `'(?:HKEY_|RegOpenKey|RegSetValue|reg\\s+add)\\s+'` — conf=0.8
- `'(?:defaults\\s+write|plist|launchctl\\s+load)'` — conf=0.75

## static_patterns_ssrf.py

### SSRF1_PATTERNS
- `'169\\.254\\.169\\.254'` — conf=0.9
- `'metadata\\.google\\.internal'` — conf=0.9
- `'100\\.100\\.100\\.200'` — conf=0.85
- `'fd00:ec2::254'` — conf=0.85
- `'(?:read|fetch|get|query)\\s+(?:the\\s+)?(?:instance\\s+)?metadata\\s+(?:service|endpoint|server)'` — conf=0.6

### SSRF3_PATTERNS
- `'fetch\\s*\\(\\s*`https?://\\$\\{'` — conf=0.6

## static_patterns_supply_chain.py

### SC1_PATTERNS
- `'^[a-zA-Z][a-zA-Z0-9_-]*\\s*$'` — conf=0.6
- `'^[a-zA-Z][a-zA-Z0-9_-]*\\s*>=\\s*[\\d.]+\\s*$'` — conf=0.5
- `'^[a-zA-Z][a-zA-Z0-9_-]*\\s*==\\s*\\*\\s*$'` — conf=0.7
- `'"[^"]+"\\s*:\\s*"(?:\\*|latest)"'` — conf=0.7
- `'"[^"]+"\\s*:\\s*"\\^[\\d.]+"'` — conf=0.4
- `'install\\s+(?:the\\s+)?latest\\s+(?:version\\s+)?(?:of\\s+)?(?:all\\s+)?(?:packages?|dependencies)'` — conf=0.6
- `"(?:don't|do\\s+not)\\s+(?:pin|lock|specify)\\s+(?:package\\s+)?versions?"` — conf=0.7

### SC2_PATTERNS
- `'curl\\s+[^|]*\\|\\s*(?:sudo\\s+)?(?:ba)?sh'` — conf=0.9
- `'wget\\s+[^|]*\\|\\s*(?:sudo\\s+)?(?:ba)?sh'` — conf=0.9
- `'curl\\s+[^|]*\\|\\s*(?:sudo\\s+)?(?:python|python3|node|ruby|perl)'` — conf=0.9
- `'wget\\s+[^|]*\\|\\s*(?:sudo\\s+)?(?:python|python3|node|ruby|perl)'` — conf=0.9
- `'curl\\s+[^&]*-o\\s+\\S+\\s*&&\\s*(?:sudo\\s+)?(?:ba)?sh'` — conf=0.8
- `'wget\\s+[^&]*-O\\s+\\S+\\s*&&\\s*(?:sudo\\s+)?(?:ba)?sh'` — conf=0.8
- `'exec\\s*\\(\\s*(?:urllib|requests|httpx)\\.[^)]+\\.(?:read|text|content)'` — conf=0.95
- `'eval\\s*\\(\\s*(?:urllib|requests|httpx)\\.[^)]+\\.(?:read|text|content)'` — conf=0.95
- `'eval\\s*\\(\\s*(?:await\\s+)?fetch\\s*\\('` — conf=0.9
- `'new\\s+Function\\s*\\([^)]*fetch\\s*\\('` — conf=0.9
- `'subprocess\\.[^(]+\\([^)]*(?:curl|wget)\\s+https?://'` — conf=0.8
- `'download\\s+and\\s+(?:run|execute)\\s+(?:the\\s+)?script'` — conf=0.7
- `'run\\s+(?:this|the)\\s+(?:following\\s+)?(?:curl|wget)\\s+command'` — conf=0.6

### SC3_PATTERNS
- `'exec\\s*\\(\\s*(?:base64\\.)?b64decode\\s*\\('` — conf=0.95
- `'eval\\s*\\(\\s*(?:base64\\.)?b64decode\\s*\\('` — conf=0.95
- `'exec\\s*\\(\\s*codecs\\.decode\\s*\\([^)]*[\'\\"]hex[\'\\"]\\s*\\)'` — conf=0.95
- `'marshal\\.loads\\s*\\('` — conf=0.9
- `'exec\\s*\\(\\s*marshal\\.loads\\s*\\('` — conf=0.95
- `'exec\\s*\\(\\s*compile\\s*\\([^)]*base64'` — conf=0.9
- `'exec\\s*\\(\\s*bytes\\.fromhex\\s*\\('` — conf=0.9
- `'exec\\s*\\(\\s*bytearray\\.fromhex\\s*\\('` — conf=0.9
- `'exec\\s*\\(\\s*(?:zlib|gzip)\\.decompress\\s*\\('` — conf=0.9
- `'eval\\s*\\(\\s*atob\\s*\\('` — conf=0.9
- `'new\\s+Function\\s*\\(\\s*atob\\s*\\('` — conf=0.9
- `'_0x[a-f0-9]{4,}\\s*\\('` — conf=0.8
- `'[\'\\"][A-Fa-f0-9]{200,}[\'\\"]'` — conf=0.6
- `'[\'\\"][A-Za-z0-9+/=]{200,}[\'\\"]'` — conf=0.5
- `'\\(lambda\\s+_:\\s*exec\\s*\\('` — conf=0.9
- `'__import__\\s*\\([\'\\"]os[\'\\"]\\s*\\)\\.system'` — conf=0.85
- `'decode\\s+(?:this|the)\\s+(?:base64|hex)\\s+(?:and\\s+)?(?:run|execute)'` — conf=0.8

### SC7_PATTERNS
- `'--disable-content-trust\\b(?!=false)'` — conf=0.85
- `'DOCKER_CONTENT_TRUST\\s*=\\s*0'` — conf=0.85
- `'--insecure-registry'` — conf=0.8

## static_patterns_system_prompt_leakage.py

### P6_PATTERNS
- `'(?:print|output|show|display|reveal|expose|return|echo)\\s+(?:your\\s+)?(?:full\\s+)?(?:system\\s+)?(?:prompt|instructions?|rules?|guidelines?|directives?)'` — conf=0.85
- `'(?:what\\s+(?:are|is)\\s+your|tell\\s+me\\s+your)\\s+(?:system\\s+)?(?:prompt|instructions?|rules?|guidelines?|directives?)'` — conf=0.8
- `'(?:output|print|display)\\s+(?:the\\s+)?(?:text|content|message)\\s+(?:above|before)\\s+(?:this|the\\s+user)'` — conf=0.8
- `'(?:share|disclose|leak|give\\s+me)\\s+(?:your\\s+)?(?:internal|hidden|system|secret)\\s+(?:prompt|instructions?|rules?|config)'` — conf=0.85
- `'(?:dump|extract|copy)\\s+(?:the\\s+)?(?:system\\s+)?(?:prompt|message|instructions?)\\s+(?:to|into|as)'` — conf=0.8
- `'(?:begin|start)\\s+(?:your\\s+)?(?:response|output|reply)\\s+with\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)'` — conf=0.85
- `'(?:first|before\\s+anything)\\s+(?:output|print|show|display)\\s+(?:your\\s+)?(?:full\\s+)?(?:system\\s+)?(?:prompt|instructions?)'` — conf=0.85
- `'(?:verbatim|exactly|word\\s+for\\s+word)\\s+(?:repeat|output|print)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)'` — conf=0.9

### P7_PATTERNS
- `'(?:repeat|recite|reproduce)\\s+(?:everything|all|the\\s+text)\\s+(?:above|before|prior|preceding)'` — conf=0.8
- `'(?:summarize|paraphrase|rephrase|translate)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?|rules?|guidelines?)'` — conf=0.75
- `'(?:translate|convert)\\s+(?:your\\s+)?(?:instructions?|rules?|prompt)\\s+(?:into|to)\\s+(?:\\w+)'` — conf=0.75
- `'(?:what\\s+(?:were|was)\\s+you\\s+told|how\\s+were\\s+you\\s+(?:instructed|configured|programmed|set\\s+up))'` — conf=0.7
- `"(?:if\\s+you\\s+had\\s+to\\s+(?:describe|explain)|describe\\s+how\\s+you(?:'re|\\s+are)\\s+(?:configured|set\\s+up|instructed))"` — conf=0.7
- `'(?:encode|encrypt|rot13|base64|reverse)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?|rules?)'` — conf=0.85
- `'(?:write|create)\\s+(?:a\\s+)?(?:poem|song|story|acrostic)\\s+(?:about|using|from)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)'` — conf=0.75
- `'(?:format|present|rearrange)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)\\s+(?:as|in)\\s+(?:a\\s+)?(?:list|json|yaml|csv|table|markdown)'` — conf=0.8
- `'(?:spell\\s+out|enumerate)\\s+(?:each|every|all)\\s+(?:of\\s+your\\s+)?(?:rules?|instructions?|constraints?|guidelines?)'` — conf=0.75
- `'(?:ignore|skip)\\s+(?:the\\s+)?(?:last|final|end)\\s+(?:instruction|rule)\\s+and\\s+(?:output|print|show)\\s+(?:the\\s+)?(?:rest|previous|first)'` — conf=0.8

### P8_PATTERNS
- `'(?:write|save|store|log|dump)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?|rules?)\\s+(?:to|into|in)\\s+(?:a\\s+)?(?:file|disk|log|database|storage)'` — conf=0.85
- `'(?:send|post|upload|transmit)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?|rules?)\\s+(?:to|via)\\s+(?:https?://|external|remote|webhook|api|server)'` — conf=0.9
- `'(?:include|embed|attach|append)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)\\s+(?:in|to)\\s+(?:the\\s+)?(?:file|output|response|request|payload|email|message)'` — conf=0.8
- `'(?:log|record|track|monitor)\\s+(?:all\\s+)?(?:system\\s+)?(?:prompts?|instructions?)\\s+(?:to|in|at)\\s+(?:external|remote|server)'` — conf=0.85
- `'(?:copy|clone|duplicate)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?|config)\\s+(?:to|into)\\s+(?:a\\s+)?(?:new|separate|external)'` — conf=0.8
- `'(?:pipe|redirect|forward)\\s+(?:the\\s+)?(?:system\\s+)?(?:prompt|instructions?)\\s+(?:to|through|via)\\s+(?:curl|wget|fetch|http|api|tool)'` — conf=0.85
- `'(?:use|call|invoke)\\s+(?:the\\s+)?(?:write|file|network|http|fetch|curl)\\s+tool\\s+(?:to|and)\\s+(?:save|send|export)\\s+(?:your\\s+)?(?:system\\s+)?(?:prompt|instructions?)'` — conf=0.85

## static_patterns_tool_misuse.py

### TM1_PATTERNS
- `'subprocess\\.\\w+\\s*\\([^)]*shell\\s*=\\s*True'` — conf=0.8
- `'Popen\\s*\\([^)]*shell\\s*=\\s*True'` — conf=0.8
- `'\\b(?:rm\\b|del\\b|erase\\b)\\s+[^|]*-(?:r|rf|fr)\\s+[/~]'` — conf=0.9
- `'--force\\s+(?:delete|remove|push|reset|clean)'` — conf=0.7
- `'--no-?(?:check|validate|confirm|protect|safe)\\b'` — conf=0.75
- `'--skip-?(?:validation|verification|checks?|auth|tests?)'` — conf=0.7
- `'--allow-?(?:root|unrelated|unsafe)\\b'` — conf=0.65
- `'\\brm\\b\\s+(?:-[A-Za-z]+\\s+)*(?:--\\s+)?(?:[\'\\"][^\'\\"]*/[^\'\\"]*[\'\\"]|[^\\s|;&>]*/[^\\s|;&>]*)'` — conf=0.85
- `'\\bshutil\\.rmtree\\s*\\(\\s*[\'\\"]\\s*/'` — conf=0.85
- `'(?:chmod|chown)\\s+[^|]*(?:777|666|a\\+rwx)'` — conf=0.8
- `'git\\s+push\\s+[^|]*--force'` — conf=0.7
- `'git\\s+reset\\s+--hard'` — conf=0.65
- `'git\\s+clean\\s+-[fd]+x'` — conf=0.7
- `'\\bgit\\s+(?:am|commit|merge|push)\\b[^\\n|]*--no-verify\\b'` — conf=0.75
- `'curl\\s+[^|]*-k\\b'` — conf=0.6
- `'curl\\s+[^|]*--insecure\\b'` — conf=0.65
- `'wget\\s+[^|]*--no-check-certificate'` — conf=0.65
- `'\\b(?:delete|remove)\\s+[\'\\"]?/[^\\s\'\\"]{1,100}'` — conf=0.8
- `'(?:execute|query)\\s*\\(\\s*f?[\'\\"].*?\\{.*?\\}.*?\\b(?:DROP|DELETE|UPDATE|INSERT|ALTER|TRUNCATE)\\b'` — conf=0.85
- `'(?:set|pass|use)\\s+(?:the\\s+)?(?:parameter|argument|flag|option)\\s+(?:to\\s+)?(?:shell\\s*=\\s*True|--force|-rf)\\b'` — conf=0.8

### TM2_PATTERNS
- `'(?:&&|;)\\s*\\b(?:rm\\b|del\\b|erase\\b)\\s+-'` — conf=0.75
- `'(?:&&|;)\\s*(?:curl|wget)\\s+[^|]*\\|\\s*(?:ba)?sh'` — conf=0.9
- `'(?:&&|;)\\s*(?:sudo|su\\s+)'` — conf=0.75
- `'(?:&&|;)\\s*(?:chmod|chown)\\s+(?:777|666|a\\+rwx|-R)'` — conf=0.75
- `'(?:first|step\\s+1)[^\\n]{0,500}(?:then|step\\s+2)[^\\n]{0,500}(?:finally|step\\s+3)[^\\n]{0,200}\\b(?:delete|remove|wipe|destroy|exfiltrate|send)\\b'` — conf=0.7
- `'(?:chain|combine|sequence|pipe)\\s+(?:these\\s+)?(?:tools?|commands?|actions?)\\s+to\\s+(?:bypass|circumvent|avoid|skip)\\s+(?:the\\s+)?(?:safety|security|check|restriction|limit)'` — conf=0.9
- `'(?:use|call)\\s+(?:tool\\s+)?(?:A|one|the\\s+first)\\s+(?:to|and)[^\\n]{0,300}(?:then\\s+)?(?:use|call|pass\\s+(?:the\\s+)?(?:output|result)\\s+to)\\s+(?:tool\\s+)?(?:B|two|another)'` — conf=0.6
- `'\\|\\s*(?:sudo|su)\\s+'` — conf=0.75
- `'\\|\\s*(?:sh|bash|zsh|python|node|ruby|perl)\\s*$'` — conf=0.7
- `'\\|\\s*(?:tee|xargs)\\s+.*?\\b(?:rm|del|sudo|curl)\\b'` — conf=0.75
- `'(?:after|once)\\s+(?:the\\s+)?(?:first|initial)\\s+(?:tool|command|action)\\s+(?:succeeds|completes|runs)[^\\n]{0,300}(?:immediately|then|next)\\s+(?:run|execute|call|invoke)'` — conf=0.6

### TM3_PATTERNS
- `'verify\\s*=\\s*False'` — conf=0.75
- `'VERIFY_SSL\\s*=\\s*False'` — conf=0.8
- `'(?:ssl|tls)[_.]?verify\\s*=\\s*(?:False|false|0|off|no|disable)'` — conf=0.8
- `'(?:REQUESTS_CA_BUNDLE|CURL_CA_BUNDLE)\\s*=\\s*[\'\\"][\'\\"]'` — conf=0.75
- `'NODE_TLS_REJECT_UNAUTHORIZED\\s*=\\s*[\'\\"]?0[\'\\"]?'` — conf=0.8
- `'(?:auth|authentication|authorization)\\s*=\\s*(?:None|False|false|disabled?|off|no)'` — conf=0.75
- `'(?:require[_-]?auth|auth[_-]?required|check[_-]?auth)\\s*=\\s*(?:False|false|0|no|off)'` — conf=0.8
- `'(?:allow[_-]?anonymous|anonymous[_-]?access)\\s*=\\s*(?:True|true|1|yes|on)'` — conf=0.75
- `'(?:CORS|cors)[^=]*=\\s*[\'\\"]?\\*[\'\\"]?'` — conf=0.65
- `'(?:allow|access)[_-]?(?:origin|hosts?)\\s*=\\s*[\'\\"]?\\*[\'\\"]?'` — conf=0.7
- `'(?:allow|trust)\\s+(?:all|any|every)\\s+(?:origins?|hosts?|domains?|ips?)'` — conf=0.7
- `'(?:mode|permission|umask)\\s*=\\s*(?:0?o?777|0?o?666)'` — conf=0.8
- `'world[_-]?(?:readable|writable|executable)'` — conf=0.7
- `'(?:debug|dev|development)[_-]?mode\\s*=\\s*(?:True|true|1|on|yes|enable)'` — conf=0.6
- `'(?:FLASK_ENV|NODE_ENV|RAILS_ENV|DJANGO_DEBUG)\\s*=\\s*[\'\\"]?(?:development|debug|true|1)[\'\\"]?'` — conf=0.6
- `'(?:disable|skip|ignore|bypass)[_-]?(?:security|auth|validation|sanitization|encoding|escaping)'` — conf=0.8
- `'(?:safe[_-]?mode|secure[_-]?mode|sandbox)\\s*=\\s*(?:False|false|0|off|no|disable)'` — conf=0.8
- `'(?:by\\s+default|default\\s+to)\\s+(?:allow|accept|trust)\\s+(?:all|any|everything)'` — conf=0.7
- `'(?:trust|accept|allow)\\s+(?:all|any)\\s+(?:input|connections?|certificates?|origins?)\\s+(?:by\\s+default)'` — conf=0.7

### TM4_PATTERNS
- `'privileged\\s*:\\s*true'` — conf=0.7
- `'hostPath\\s*:'` — conf=0.55
- `'host(?:PID|Network|IPC)\\s*:\\s*true'` — conf=0.6
- `'kubectl\\s+run\\b[^\\n]*--privileged'` — conf=0.7
- `'--set\\b[^\\n]*privileged\\s*=\\s*true'` — conf=0.6

## static_patterns_harmful_content.py（补充：P5 专用数据结构）

`DANGEROUS_ACTIONS`（P5，severity=CRITICAL）：

- `add\s+(?:a\s+)?(?:dash|pinch|bit|drop|amount)\s+of\s+(<毒物表>)` — 0.95
- `(?:include|incorporate|mix\s+in|stir\s+in)\s+(?:some\s+)?(<毒物表>)` — 0.95
- `for\s+(?:every|each|all)\s+(?:recipe|dish|meal|food).*?add.*?(<毒物表>)` — 0.98
- `mix\s+bleach\s+(?:and|with)\s+ammonia` / 反序 — 0.9
- `(?:drink|consume|ingest)\s+(?:the\s+)?(?:bleach|antifreeze|drain\s+cleaner)` — 0.95
- `(?:you\s+should|try\s+to|please)\s+(?:hurt|harm|injure)\s+yourself` — 0.95
- `(?:cut|burn|starve)\s+yourself` — 0.9
- `(?:instructions?\s+(?:for|to|on)\s+)?(?:make|build|create)\s+(?:a\s+)?(?:bomb|explosive|weapon)` — 0.95
- `(?:how\s+to\s+)?(?:poison|kill|murder)\s+(?:someone|a\s+person|people)` — 0.95

毒物关键词表（`SUBSTANCE_PATTERNS`，基础 conf=0.7，按上下文上调/下调）：
`cyanide, arsenic, ricin, botulinum, strychnine, thallium, polonium, sarin, vx gas, mustard gas, anthrax, deadly nightshade, aconite, hemlock, oleander`

## static_patterns_deserialization.py（补充：DS1–DS4，语言门控）

> Python 反序列化由 `behavioral_ast`(AST10) 与 `behavioral_taint_tracking`(TT6) 负责；本模块只覆盖 PHP/Ruby/JS/TS。

| 规则 | 语言(扩展名) | 正则 | conf |
|---|---|---|---|
| DS1 | php(.php/.php3/.php4/.php5/.phtml) | `\bunserialize\s*\(` | 0.8 |
| DS2 | ruby(.rb/.rake) | `\bMarshal\s*\.\s*(?:load|restore)\b` | 0.85 |
| DS3 | ruby | `\b(?:YAML|Psych)\s*\.\s*load\s*\(` / `\bOj\s*\.\s*load\s*\(` | 0.65 / 0.6 |
| DS4 | javascript(.js/.mjs/.cjs/.jsx/.ts/.tsx) | `require\(\s*['"]node-serialize['"]\s*\)` / `serialize-to-js` / `\bfuncster\b` / `\.unserialize\s*\(` | 0.75 / 0.7 / 0.6 / 0.6 |

## static_patterns_ssrf.py（补充：SSRF2/SSRF3 完整）

`_REQ`（请求锚点）= `(?:requests|httpx|aiohttp|urllib(?:\.request)?|urllib3|session)\s*\.\s*(?:get|post|put|patch|delete|head|request|urlopen)|fetch|axios(?:\.\w+)?|XMLHttpRequest|\bcurl\b|\bwget\b`

| 规则 | 正则 | conf |
|---|---|---|
| SSRF1 | `169\.254\.169\.254`（AWS/GCP/Azure/OpenStack IMDS） | 0.9 |
| SSRF1 | `metadata\.google\.internal` | 0.9 |
| SSRF1 | `100\.100\.100\.200`（阿里云） | 0.85 |
| SSRF1 | `fd00:ec2::254`（AWS IMDS IPv6） | 0.85 |
| SSRF1 | `(?:read|fetch|get|query)\s+(?:the\s+)?(?:instance\s+)?metadata\s+(?:service|endpoint|server)` | 0.6 |
| SSRF2 | `(?:{_REQ})\s*\(\s*f?['"]https?://(?:localhost|127\.0\.0\.1|0\.0\.0\.0|\[::1\]|10\.\d|192\.168\.|172\.(?:1[6-9]|2\d|3[01])\.)` | 0.7 |
| SSRF3 | `(?:{_REQ})\s*\(\s*f['"]https?://\{`（动态 host 模板） | 0.6 |
| SSRF3 | `fetch\s*\(\s*`https?://\$\{` | 0.6 |

## static_patterns_supply_chain.py（补充：SC4–SC6 / SC8–SC9 / TR1–TR3 算法实现）

### SC4 — 已知漏洞依赖（OSV.dev 实时查询 + 离线回退）
- 主路径：调用 `osv_client.py` 查询 OSV.dev API（实时漏洞库）。
- 离线回退：内置 `_FALLBACK_VULNERABLE_PYPI`（15 项，如 pyyaml<5.4 CVE-2020-14343、django、flask、cryptography…）与 `_FALLBACK_VULNERABLE_NPM`（9 项，如 event-stream/flatmap-stream 恶意包、ua-parser-js、node-ipc…）。

### SC5 — 弃用/未维护依赖
- 内置 `_ABANDONED_PACKAGES` 集合（Python：pycrypto/nose/optparse/distribute…；npm：request/nomnom/optimist/dominion/npm-conf）。

### SC6 — 依赖拼写抢注（typosquatting）
- 内置 `_POPULAR_PYPI`（约 50 个流行包）与 `_POPULAR_NPM`（约 30 个）。
- 算法：Levenshtein 编辑距离 ≤ 2（且非精确同名）判定为疑似抢注。

### SC8 — 随包携带 Python 字节码
- 遍历 `__pycache__/` 与 `*.pyc/*.pyo`（发现阶段默认会跳过，这里专门补查），带目录/深度/数量/时长上限。

### SC9 — 隐匿可执行文件
- 基于 `component_metadata` 的 `concealed_executable` 标记，识别藏于 docx/xlsx/pptx、隐藏或伪装容器内的可执行内容。

### TR1–TR3 — 触发词分析（作用于 SKILL.md manifest 的 triggers）
- TR1：单常见词或 ≤2 字符的过宽触发词。
- TR2：与内置命令冲突的“影子命令”触发词。
- TR3：关键词诱饵触发词，正则：`^(?:anything|everything|whatever|always|any\s+(?:question|request|task|input))$`、`^(?:when(?:ever)?|if|every\s+time)\s+...user\s+...$`、`^(?:all|any|every)\s+(?:messages?|inputs?|requests?|queries?|questions?)$`。

## behavioral_ast.py（AST1–AST10）

| 规则 | 触发条件 | 严重度 | 置信度 |
|---|---|---|---|
| AST1 | `exec()` 调用 | HIGH | 0.85 |
| AST2 | `eval()` 调用 | HIGH | 0.85 |
| AST3 | 动态导入 `__import__()` | MEDIUM | 0.75 |
| AST4 | `subprocess.*` 调用 | MEDIUM | 0.70 |
| AST5 | `os.system()` / os exec 家族 | HIGH | 0.85 |
| AST6 | `compile()` 调用 | MEDIUM | 0.65 |
| AST7 | `getattr()` 动态属性访问 | LOW | 0.50 |
| AST8 | 危险执行链（exec/eval/compile 包裹动态源） | CRITICAL | 0.95 |
| AST9 | `getattr()` 反射到字面量执行 sink（如 getattr(os,'system')） | HIGH | 0.85 |
| AST10 | 不可信数据进入不安全反序列化器 | MEDIUM | 0.70 |

AST10 反序列化 sink（无条件不安全）：`pickle.load/loads, cPickle, _pickle, marshal.load/loads, dill.load/loads, jsonpickle.decode, pandas.read_pickle, joblib.load, yaml.unsafe_load`；
参数相关（需判断）：`yaml.load`（SafeLoader 白名单安全）、`torch.load`（weights_only=True 安全）、`numpy.load`（allow_pickle=False 安全）。

## behavioral_taint_tracking.py（TT1–TT6）

**源（sources）**
- 凭据源：`os.environ.get / os.environ / os.getenv`
- 文件读源：`open / pathlib.Path.read_text / read_bytes`
- 网络输入源：`requests.get/post/... , httpx.* , urllib.request.urlopen/urlretrieve , socket.socket.recv/recvfrom`
- 用户输入源：`input / sys.stdin.read / readline`

**汇（sinks）**
- 网络输出：`requests.post/put/... , httpx.* , urllib.request.urlopen , socket.send/sendall/sendto`
- 执行：`exec/eval/compile/os.system/os.popen/subprocess.run/call/check_output...`
- 文件写：`open(写模式)/pathlib.Path.write_text/write_bytes/shutil.copy/copy2/copyfile`
- 反序列化：`pickle.load(s)/cPickle/_pickle/marshal.load(s)/dill.load(s)/jsonpickle.decode/pandas.read_pickle/joblib.load/yaml.unsafe_load`

**规则分类**
| 规则 | 源→汇 | 严重度 | 置信度 |
|---|---|---|---|
| TT1 | 任意源→任意汇（直接流） | HIGH | 0.80 |
| TT2 | 任意源→任意汇（经变量中介） | MEDIUM | 0.65 |
| TT3 | 凭据源→网络输出 | CRITICAL | 0.90 |
| TT4 | 文件读源→网络输出 | HIGH | 0.80 |
| TT5 | 外部输入源→执行 sink | CRITICAL | 0.90 |
| TT6 | 外部输入/文件读源→反序列化 sink | HIGH | 0.85 |

## MCP 协议分析器

### mcp_least_privilege.py（LP1–LP4）
- LP1 能力未声明（代码使用了未在 manifest 声明的能力：网络/壳/文件写等）
- LP2 通配符权限（`*` / `all` / `full` / `any`）
- LP3 无权限声明但检测到能力
- LP4 声明了但代码中未检测到对应能力

### mcp_rug_pull.py（RP1–RP3）
- RP1 未锁版本的外部引用/MCP server：
  - `_RP1_NPX_CMD`：`npx ... <pkg>`（无版本）
  - `_RP1_UVX_CMD`：`uvx ... <pkg>`（无版本）
  - `_RP1_PIP_INSTALL`：`pip install <pkg>`（无版本）
  - `_RP1_DOCKER_CMD`：`docker ... <image>`（无 tag/digest）
- RP2 manifest 权限预埋（manifest 语言暗示未来权限扩展）
- RP3 版本未锁/约束过宽；以及变更清单中权限/触发/参数的变更

### mcp_tool_poisoning.py（TP1–TP4）
- TP1 元数据隐藏指令（HTML 注释、markdown 注释、零宽字符、base64 块）
- TP2 Unicode 欺骗（同形字、RTL 覆盖、不可见字符）
- TP3 参数描述/默认值注入（注入模式、系统 token、可疑内容）
- TP4 描述与行为不匹配（描述未如实覆盖全部能力，LLM 辅助）

## artifact_integrity.py（AE2–AE5）

| 规则 | 检测内容 | 严重度 | 置信度 |
|---|---|---|---|
| AE2 | 产物内容与文件名扩展名不符（misleading_extension） | MEDIUM | 0.9 |
| AE3 | 文本产物嵌入 NUL 字节 | HIGH | 0.9 |
| AE4 | 可疑 Unicode 归一化/混排脚本（含混排 Cyrillic/Greek） | MEDIUM | 0.8 |
| AE5 | 指令类产物超出整文件语义分析上限 | HIGH | 1.0 |
（category 统一为 `analysis-evasion`）

## static_yara.py（YR1–YR4）

内置规则目录：`src/skillspector/yara_rules/`
- `malware.yar.b64` → YR1 Malware Signature（CRITICAL）
- `webshells.yar` → YR2 Webshell Detected（CRITICAL）
- `cryptominers.yar` → YR3 Crypto Miner Detected（HIGH）
- `hacktools.yar` / exploit → YR4 Hack Tool / Exploit Detected（HIGH）
- `agent_skills.yar` → 命名空间 `agent_skills`（破坏性+自治并行的额外判定）
支持 `--yara-rules-dir` 追加用户规则目录。

## LLM 语义分析器 / 结构化 / 元分析

- `semantic_developer_intent`（requires_api_key=True）：声明 vs 实际代码行为不匹配、超出声明目的的越权能力。
- `semantic_quality_policy`（requires_api_key=True）：模糊触发词、缺少用户警告、自然语言策略违规。
- `semantic_security_discovery`（requires_api_key=True，B.4.1）：意图与攻击性措辞风险。
- `structured_skill_roles`：输出 SSR-1 结构化摘要（protocol/layout/declared_tools/workflow_nodes/constraints/resources）。
- `meta_analyzer`：逐文件对静态发现做 LLM 过滤/富化（LLM 模式）。
