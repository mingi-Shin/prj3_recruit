document.addEventListener('DOMContentLoaded', function() {
	const fileInput = document.getElementById('fileInput');

	//이력서 추가 버튼 클릭시 새 페이지로 이동
	document.getElementById('addResumeBtn').addEventListener('click', function() {
		
		//이력서 10개까지만 제한
		const resumeCnt = document.querySelectorAll('.card').length;
		if(resumeCnt >= 11){
			alert("이력서는 최대 10개까지만 작성 가능합니다.")
			return;
		}
		
		location.href = '/user/resume/resume_create';
	});

	//이력서 수정 및 삭제 버튼 클릭시
	document.getElementById('resumeContainer').addEventListener('click', function(e) {
		const card = e.target.closest('.card');
		const resumeSeq = card.getAttribute('data-resumeSeq');

		//수정
		const editBtn = e.target.closest('.edit-btn');
		if (editBtn) {
			location.href = '/user/resume/resume_form/' + resumeSeq;
			return;
		}

		//삭제
		const deleteBtn = e.target.closest('.delete-btn');
		if (deleteBtn) {
			if (confirm('이력서를 정말 삭제하시겠습니까?')) {
				fetch('/user/resume/resumeRemove/' + resumeSeq, {
					method: 'POST'
				})
					.then(response => {
						if (!response.ok) {
							throw new Error('서버 오류 : ' + response.status)
						}
						return response.json();
					})
					.then(result => {
						if (result.result === 'success') {
							card.remove();
						}
					})
					.catch(error => {
						alert('요청 실패 : ' + error.message)
					});
			}
		}

		//다운로드
		const downloadBtn = e.target.closest('.download-btn');
		if(downloadBtn){
			location.href = '/user/resume/download/' + resumeSeq;
		}

	});



	//첨부파일 추가 버튼 클릭시
	document.getElementById('addFileBtn').addEventListener('click', function() {
		fileInput.click();
	});

	//파일 선택시
	document.getElementById('fileInput').addEventListener('change', function(e) {
		const selectedFile = fileInput.files[0];

		if (!selectedFile) return;

		const formData = new FormData();
		formData.append('file', selectedFile);

		//AJAX로 서버랑 통신
		fetch('/user/attachment', {
			method: 'POST',
			body: formData
		})
			.then(response => {
				if (!response.ok) {
					throw new Error('서버 오류: ' + response.status);
				}
				return response.json();
			})
			.then(result => {
				if (result.result == 'success') {
					const file = result.file;
					const container = document.getElementById('fileListContainer');
					container.innerHTML += `
					<div class="file-item" data-value="${file.attachmentSeq}">
					<div class="file-info">
						<div class="file-icon">${file.fileType}</div>
						<div class="file-details">
							<div class="file-name download">${file.originalName}</div>
							<div class="file-date">${file.createdAt}</div>
						</div>
					</div>
					<div class="file-actions">
						<button class="action-btn delete">
							<i class="fas fa-trash-alt"></i>
						</button>
					</div>
				</div>
				`;
				} else {
					alert('동일한 이름의 파일이 이미 업로드 되어 있습니다.\n파일명 변경후 다시 시도해 주세요.');
				}
			})
			.catch(error => {
				alert('요청 실패 : ' + error.message);
			})
	});

	//다운 및 삭제 버튼 클릭시
	document.getElementById('fileListContainer').addEventListener('click', function(e) {
		const fileItem = e.target.closest('.file-item');
		const attachmentSeq = fileItem.getAttribute('data-value');

		//다운
		const downloadBtn = e.target.closest('.download');
		if (downloadBtn) {
			// 다운로드 링크 생성하여 클릭
			const downloadLink = document.createElement('a');
			downloadLink.href = '/user/attachment/download/' + attachmentSeq;
			downloadLink.download = ''; // 서버에서 지정한 파일명 사용
			document.body.appendChild(downloadLink);
			downloadLink.click();
			document.body.removeChild(downloadLink);
			return;
		}
		
		//삭제
		const deleteBtn = e.target.closest('.delete');
		if (deleteBtn) {

			if (confirm('정말 파일을 삭제하시겠습니까?')) {
				fetch('/user/attachment/' + attachmentSeq, {
					method: 'DELETE'
				})
					.then(response => {
						if (!response.ok) {
							throw new Error('서버 오류 : ' + response.status)
						}
						return response.json();
					})
					.then(result => {
						if (result.result === 'success') {
							fileItem.remove();
						}
					})
					.catch(error => {
						alert('요청 실패 : ' + error.message)
					});
			}
			return;
		}
	});

});