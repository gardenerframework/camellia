 --稍微注释一下以防忘掉
--这里是要将每一个与当前授权id相关的token的值更新
--先是授权码，然后是授权令牌，然后是id令牌，然后是刷新令牌
--每一个令牌都是一个key，从2开始，不存在的key用空字符串占位
--每一个令牌的过期时间都是一个arg，如果令牌不存在，用空字符串来占位
local ttl = ARGV[3]
local id = KEYS[1]
redis.call('set', id, ARGV[1], 'ex', ttl)

--删除旧的索引
for i = 2, 6 do
    if KEYS[i] ~= "" then
        redis.call('del', KEYS[i])
    end
end

local stateKey = KEYS[7]
local stateKeyTtl = ARGV[2]

if stateKey ~= "" then
    redis.call('set', stateKey, id, 'ex', stateKeyTtl)
end

--设置新的索引
for i = 8, 11 do
    if KEYS[i] ~= "" then
        redis.call('set', KEYS[i], id, 'ex', ttl)
    end
end
