/**
 * 
 */

//mongodb会将map.js中的函数返回的MultiValueMap对象的键值对逐一调用reduce.js中的函数，该函数的参数
//分别对应该map对象的键值对。返回结果为一个键值对类型的对象，这里用ValueObject表示
function (key, values) {
    var sum = 0;
    for (var i = 0; i < values.length; i++)
        sum += values[i];
    return sum;
}


