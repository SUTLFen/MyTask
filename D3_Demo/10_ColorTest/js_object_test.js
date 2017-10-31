/**
 * Created by Fairy_LFen on 2017/5/17.
 */
function Person(name, age){
    this.name = name;
    this.age = age;

    console.log("helolooooooooooo");
    this.desp = function(d){
        console.log("name : " + this.name);
        console.log("age : "+ this.age);
        console.log("d : " + d);
    };
}