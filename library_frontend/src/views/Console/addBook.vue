<template>
<form @submit.prevent="handleSubmit">
    <label>标题:</label>
    <input type="text" required v-model="title">

    <label>作者:</label>
    <input type="text" required v-model="author">

    <label>简介:</label>
    <input type="text" required v-model="desc">

    <label>类型:(按下空格键加入一个标签)</label>
    <input type="text" v-model="tempSkill" @keyup="addSkill">
    <div v-for="skill in skills" :key="skill" class="pill">
        <span @click="deleteSkill(skill)">{{ skill }}</span>
    </div>

    <div>
      <input type="checkbox" required v-model="terms">
      <label>确认信息无误</label>
    </div>

    <div class="submit" @click="book">
      <button>添加书籍</button>
    </div>
  </form>
</template>

<script>
import { addBook } from '@/net';
export default {
  components: {},
  props: {},
  data() {
    return {
      title:'',
      author:'',
      desc:"",
      terms:false,
      tempSkill:'',
      skills:[],
    };
  },
  methods: {
    addSkill(e){
      if(e.key === ' ' && this.tempSkill){
        if(!this.skills.includes(this.tempSkill)){
          this.skills.push(this.tempSkill)
        }
        this.tempSkill = ''
      }
    },
    deleteSkill(skill){
      this.skills = this.skills.filter((item)=>{
          return skill !== item
      })
    },
    handleSubmit(){
      console.log(this.skills)
    },
    book(){
      let str=''
      for(let i=0;i<this.skills.length;i++){
        str+=this.skills[i]
      }
      addBook(this.title,this.author,this.desc,str,()=>{
        window.location.reload();
      })
  
    }
  },
};
</script>

<style>
form {
    max-width: 420px;
    margin: 30px auto;
    background: white;
    text-align: left;
    padding: 40px;
    border-radius: 10px;
  }
  label {
    color: #aaa;
    display: inline-block;
    margin: 25px 0 15px;
    font-size: 0.6em;
    text-transform: uppercase;
    letter-spacing: 1px;
    font-weight: bold;
  }
  input ,select{
    display: block;
    padding: 10px 6px;
    width: 100%;
    box-sizing: border-box;
    border: none;
    border-bottom: 1px solid #ddd;
    color: #555;
  }
  input[type="checkbox"] {
    display: inline-block;
    width: 16px;
    margin: 0 10px 0 0;
    position: relative;
    top: 2px;
  }
  .pill {
    display: inline-block;
    margin: 20px 10px 0 0;
    padding: 6px 12px;
    background: #eee;
    border-radius: 20px;
    font-size: 12px;
    letter-spacing: 1px;
    font-weight: bold;
    color: #777;
    cursor: pointer;
  }
  button {
    background: #0b6dff;
    border: 0;
    padding: 10px 20px;
    margin-top: 20px;
    color: white;
    border-radius: 20px;
  }
  .submit {
    text-align: center;
  }
  .error {
    color: #ff0062;
    margin-top: 10px;
    font-size: 0.8em;
    font-weight: bold;
  }
</style>