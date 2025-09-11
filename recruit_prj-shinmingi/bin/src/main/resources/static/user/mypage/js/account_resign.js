document.addEventListener('DOMContentLoaded', function(){
	
	document.getElementById('btn').addEventListener('click', async function(){
		
		const checkbox = document.getElementById('agreement');
		
		if(!checkbox.checked){
			alert("진행하려면 약관에 동의해 주세요.");
			return;
		}
		
		if(!confirm("정말 탈퇴하시겠습니까?")) return;
		
		try{
			const response = await fetch('/mypage/member', {
				method : 'DELETE'
			});
			
			if(!response.ok) throw new Error("서버 오류 발생");
			
			const result = await response.text();
			
			if(result === 'success'){
				alert("정상적으로 탈퇴 처리되었습니다.\n메인페이지로 이동합니다.")
				location.href = '/logout';
			} else{
				alert("탈퇴에 실패했습니다.\n잠시 후 다시 시도해주세요")
			}
		} catch(err){
			console.log(err);
		}
		
	});
});
	
