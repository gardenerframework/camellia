--这个逻辑是用反向索引找到id，然后读取令牌

local id = redis.call('get', KEYS[1])
if type(id) == "boolean" and not id then
    return nil;
else
    return redis.call('get', id)
end